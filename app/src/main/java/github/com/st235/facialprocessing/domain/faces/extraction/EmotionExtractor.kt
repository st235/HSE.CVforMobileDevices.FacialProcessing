package github.com.st235.facialprocessing.domain.faces.extraction

import android.graphics.Bitmap
import androidx.annotation.WorkerThread
import github.com.st235.facialprocessing.R
import github.com.st235.facialprocessing.utils.tflite.InterpreterFactory
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp

@WorkerThread
class EmotionExtractor(
    interpreterFactory: InterpreterFactory
) {
    private companion object {
        const val INPUT_IMAGE_SIZE = 224
    }

    enum class Emotion(val index: Int) {
        ANGER(0),
        DISGUST(1),
        FEAR(2),
        HAPPINESS(3),
        NEUTRAL(4),
        SADNESS(5),
        SURPRISE(6),
        UNKNOWN(-1),
    }

    private val inputImageProcessor =
        ImageProcessor.Builder()
            .add(ResizeOp(INPUT_IMAGE_SIZE, INPUT_IMAGE_SIZE, ResizeOp.ResizeMethod.BILINEAR))
            .add(NormalizeOp(0f, 255f))
            .build()

    private val interpreter: Interpreter = interpreterFactory.create(R.raw.emotions_mobilenet_7)

    fun predict(image: Bitmap): Emotion {
        val tensorInputImage = TensorImage.fromBitmap(image)
        val emotionsOutputArray = Array(1){ FloatArray(7) }
        val processedImageBuffer = inputImageProcessor.process(tensorInputImage).buffer
        interpreter.run(
            processedImageBuffer,
            emotionsOutputArray
        )

        val maxIndex = emotionsOutputArray[0].indices.maxBy { emotionsOutputArray[0][it] }
        return Emotion.entries.find { it.index == maxIndex } ?: Emotion.UNKNOWN
    }

}
