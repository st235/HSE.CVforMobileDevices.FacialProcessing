package github.com.st235.facialprocessing.domain

import android.content.Context
import android.graphics.Bitmap
import androidx.annotation.RawRes
import androidx.annotation.WorkerThread
import github.com.st235.facialprocessing.domain.FaceDetector.Box.Companion.adjustLetterBoxPadding
import github.com.st235.facialprocessing.domain.FaceDetector.Box.Companion.rescaleToBitmapSize
import github.com.st235.facialprocessing.utils.clamp
import github.com.st235.facialprocessing.utils.createDataByteBuffer
import github.com.st235.facialprocessing.utils.loadModelFromRawResources
import github.com.st235.facialprocessing.utils.scaleLetterBox
import github.com.st235.facialprocessing.utils.sigmoid
import github.com.st235.facialprocessing.utils.writeToByteBuffer
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.gpu.CompatibilityList
import org.tensorflow.lite.gpu.GpuDelegate
import org.tensorflow.lite.gpu.GpuDelegateFactory
import java.nio.ByteBuffer
import kotlin.math.max
import kotlin.math.min

class FaceDetector(
    @RawRes private val rawModelFile: Int,
    interpreterFactory: InterpreterFactory,
    private val inputTensorIndex: Int = DEFAULT_INPUT_TENSOR_INDEX,
) {
    private companion object {
        const val DEFAULT_INPUT_TENSOR_INDEX = 0

        // Default for Bitmap.
        const val NUM_BYTES_PER_CHANNEL = 4
    }

    internal data class Anchor(
        val x: Float,
        val y: Float,
    ) {
        companion object {
            fun generateAnchors(
                inputSizeWidth: Int,
                inputSizeHeight: Int,
                numLayers: Int = 1,
                anchorOffsetX: Float = 0.5f,
                anchorOffsetY: Float = 0.5f,
                strides: IntArray = intArrayOf(4),
                interpolatedScaleAspectRatio: Float = 0.0f,
            ): List<Anchor> {
                val anchors = mutableListOf<Anchor>()
                var layerId = 0

                while (layerId < numLayers) {
                    var lastSameStrideLayer = layerId
                    var repeats = 0

                    while (lastSameStrideLayer < numLayers && strides[lastSameStrideLayer] == strides[layerId]) {
                        lastSameStrideLayer += 1

                        repeats += if (interpolatedScaleAspectRatio == 1.0f) {
                            2
                        } else {
                            1
                        }
                    }

                    val stride = strides[layerId]
                    val featureMapWidth = inputSizeWidth / stride
                    val featureMapHeight = inputSizeHeight / stride

                    for (y in 0 until featureMapHeight) {
                        val yCenter = (y + anchorOffsetY) / featureMapHeight
                        for (x in 0 until featureMapWidth) {
                            val xCenter = (x + anchorOffsetX) / featureMapWidth

                            for (r in 0 until repeats) {
                                anchors.add(Anchor(xCenter, yCenter))
                            }
                        }
                    }

                    layerId = lastSameStrideLayer
                }

                return anchors
            }

        }
    }

    data class Box(
        val xMin: Float,
        val yMin: Float,
        val xMax: Float,
        val yMax: Float,
        val score: Float,
    ) {
        val width: Float
            get() {
                return xMax - xMin
            }

        val height: Float
            get() {
                return yMax - yMin
            }

        companion object {
            // Score limit is 100 in mediapipe and leads to overflows with IEEE 754 floats
            // this lower limit is safe for use with the sigmoid functions and float32.
            const val RAW_SCORE_LIMIT = 80f

            // Threshold for confidence scores
            const val MIN_SCORE = 0.5f

            // NMS similarity threshold
            const val MIN_SUPPRESSION_THRESHOLD = 0.3f

            private fun normaliseScore(rawScore: Float): Float {
                val clippedScore = clamp(rawScore, -RAW_SCORE_LIMIT, RAW_SCORE_LIMIT)
                return sigmoid(clippedScore)
            }

            fun Box.adjustLetterBoxPadding(padding: FloatArray): Box {
                val left = padding[0]
                val top = padding[1]
                val right = padding[2]
                val bottom = padding[3]

                val horizontalScale = 1.0f - left - right
                val verticalScale = 1.0f - top - bottom

                return Box(
                    xMin = (xMin - left) / horizontalScale,
                    yMin = (yMin - top) / verticalScale,
                    xMax = (xMax - left) / horizontalScale,
                    yMax = (yMax - top) / verticalScale,
                    score = score,
                )
            }

            fun Box.rescaleToBitmapSize(bitmap: Bitmap): Box {
                return Box(
                    xMin = xMin * bitmap.width,
                    yMin = yMin * bitmap.height,
                    xMax = xMax * bitmap.width,
                    yMax = yMax * bitmap.height,
                    score = score,
                )
            }

            internal fun decodeBox(
                scaleX: Float,
                scaleY: Float,
                anchor: Anchor,
                rawBox: FloatArray,
                rawScore: Float
            ): Box {
                // rawBox is [x_center, y_center, w, h and face landmarks]
                val centerX = rawBox[0] / scaleX + anchor.x
                val centerY = rawBox[1] / scaleY + anchor.y

                val width = rawBox[2] / scaleX
                val height = rawBox[3] / scaleY

                return Box(
                    xMin = centerX - width / 2,
                    yMin = centerY - height / 2,
                    xMax = centerX + width / 2,
                    yMax = centerY + height / 2,
                    score = normaliseScore(rawScore),
                )
            }
        }
    }

    private val interpreter: Interpreter

    private val anchors: List<Anchor>

    private val inputWidth: Int
    private val inputHeight: Int

    private val inputByteBuffer: ByteBuffer
    private val outputMap: Map<Int, ByteBuffer>

    init {
        interpreter = interpreterFactory.create(rawModelFile)

        val inputTensor = interpreter.getInputTensor(inputTensorIndex)

        // For this model 4-dimensional vector is expected,
        // for example, [1, 192, 192, 3].
        inputWidth = inputTensor.shape()[1]
        inputHeight = inputTensor.shape()[2]
        val inputChannels = inputTensor.shape()[3]

        inputByteBuffer =
            createDataByteBuffer(capacity = inputWidth * inputHeight * inputChannels * NUM_BYTES_PER_CHANNEL)

        val mutableOutputMap = mutableMapOf<Int, ByteBuffer>()
        for (i in 0 until interpreter.outputTensorCount) {
            val shape = interpreter.getOutputTensor(i).shape()
            mutableOutputMap[i] =
                createDataByteBuffer(capacity = shape[1] * shape[2] * NUM_BYTES_PER_CHANNEL)
        }
        outputMap = mutableOutputMap

        anchors = Anchor.generateAnchors(
            inputSizeWidth = inputWidth,
            inputSizeHeight = inputHeight,
        )
    }

    @WorkerThread
    fun detect(bitmap: Bitmap): List<Box> {
        val normalisedPaddings = floatArrayOf(1f, 1f, 1f, 1f)
        val processedBitmap = if (bitmap.width != inputWidth || bitmap.height != inputHeight) {
            bitmap.scaleLetterBox(inputWidth, inputHeight, normalisedPaddings)
        } else {
            bitmap
        }

        processedBitmap.writeToByteBuffer(inputByteBuffer) { pixelValue ->
            val minValue = -1f
            val maxValue = 1f
            (pixelValue * (maxValue - minValue) / 255f) + minValue
        }

        val inputs = arrayOf(inputByteBuffer)

        interpreter.runForMultipleInputsOutputs(inputs, outputMap)

        val boxes = mutableListOf<Box>()

        // Count of features for boxes and scores should be the same.
        val outputFeatures = interpreter.getOutputTensor(0).shape()[1]
        val boxesPointsCount = interpreter.getOutputTensor(0).shape()[2]

        // Rewind boxes.
        outputMap.getValue(0).rewind()
        // Rewind scores.
        outputMap.getValue(1).rewind()

        for (i in 0 until outputFeatures) {
            val rawBox = FloatArray(size = boxesPointsCount)
            for (j in 0 until boxesPointsCount) {
                rawBox[j] = outputMap.getValue(0).getFloat()
            }
            val rawScore = outputMap.getValue(1).getFloat()

            boxes.add(
                Box.decodeBox(
                    scaleX = inputWidth.toFloat(),
                    scaleY = inputHeight.toFloat(),
                    anchor = anchors[i],
                    rawBox = rawBox,
                    rawScore = rawScore
                )
            )
        }

        val prunedDetections = nonMaximumSuppression(boxes
            .filter { it.xMax > it.xMin && it.yMax > it.yMin }
            .filter { it.score > Box.MIN_SCORE },
            minSuppressionThreshold = Box.MIN_SUPPRESSION_THRESHOLD,
            minScore = Box.MIN_SCORE
        )

        return prunedDetections
            .map { it.adjustLetterBoxPadding(normalisedPaddings) }
            .map { it.rescaleToBitmapSize(bitmap) }
    }

    private fun nonMaximumSuppression(
        detectedBoxes: List<Box>,
        minSuppressionThreshold: Float,
        minScore: Float,
    ): List<Box> {
        val output = mutableListOf<Box>()

        val remainingBoxDetections = detectedBoxes.toMutableList()
        remainingBoxDetections.sortByDescending { it.score }

        while (remainingBoxDetections.isNotEmpty()) {
            val detectionBox = remainingBoxDetections.first()

            if (detectionBox.score < minScore) {
                break
            }

            val remaining = mutableListOf<Box>()
            val candidates = mutableListOf<Box>()

            for (remainingBox in remainingBoxDetections) {
                val iou = iou(remainingBox, detectionBox)
                if (iou > minSuppressionThreshold) {
                    candidates.add(remainingBox)
                } else {
                    remaining.add(remainingBox)
                }
            }

            var totalScore = 0f
            var xMin = 0f
            var xMax = 0f
            var yMin = 0f
            var yMax = 0f
            for (candidateBox in candidates) {
                totalScore += candidateBox.score

                xMin += candidateBox.xMin * candidateBox.score
                xMax += candidateBox.xMax * candidateBox.score
                yMin += candidateBox.yMin * candidateBox.score
                yMax += candidateBox.yMax * candidateBox.score
            }

            xMin /= totalScore
            xMax /= totalScore
            yMin /= totalScore
            yMax /= totalScore

            output.add(Box(xMin, yMin, xMax, yMax, detectionBox.score))

            if (remaining.size == remainingBoxDetections.size) {
                break
            }

            remainingBoxDetections.clear()
            remainingBoxDetections.addAll(remaining)
        }

        return output
    }

    private fun iou(box1: Box, box2: Box): Float {
        val newMinX = max(box1.xMin, box2.xMin)
        val newMinY = max(box1.yMin, box2.yMin)
        val newMaxX = min(box1.xMax, box2.xMax)
        val newMaxY = min(box1.yMax, box2.yMax)

        if (newMaxY <= newMinY || newMaxX <= newMinX) {
            return 0f
        }

        val intersection = (newMaxY - newMinY) * (newMaxX - newMinX)
        val union = box1.width * box1.height + box2.width * box2.height - intersection

        return intersection / union
    }

}
