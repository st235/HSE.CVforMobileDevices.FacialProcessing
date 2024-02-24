package github.com.st235.facialprocessing.domain

import android.graphics.Bitmap
import android.util.Log
import androidx.annotation.FloatRange
import github.com.st235.facialprocessing.R
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import java.util.Arrays

class FacialAttributesExtractor(
    interpreterFactory: InterpreterFactory,
    @FloatRange(from = 0.0, to = 1.0) private val eyeglassesThreshold: Float = 0.0191f,
    @FloatRange(from = 0.0, to = 1.0) private val mustacheThreshold: Float = 0.07f,
    @FloatRange(from = 0.0, to = 1.0) private val noBeardThreshold: Float = 0.87f,
    @FloatRange(from = 0.0, to = 1.0) private val smilingThreshold: Float = 0.41f,
) {

    private companion object {
        const val INPUT_IMAGE_SIZE = 224
    }

    data class FacialAttributes(
        val hasEyeglasses: Boolean,
        val hasMustache: Boolean,
        val hasBeard : Boolean,
        val isSmiling: Boolean,
    )

    private val inputImageProcessor =
        ImageProcessor.Builder()
            .add(ResizeOp(INPUT_IMAGE_SIZE, INPUT_IMAGE_SIZE, ResizeOp.ResizeMethod.BILINEAR))
            .add(NormalizeOp(0f, 255f))
            .build()

    private val interpreter: Interpreter = interpreterFactory.create(R.raw.model_mobilenetv3_large_eyeglasses_mustache_nobeard_smiling)

    fun predict(image: Bitmap): FacialAttributes {
        val tensorInputImage = TensorImage.fromBitmap(image)
        val facialAttributesOutputArray = Array(1){ FloatArray(4) }
        val processedImageBuffer = inputImageProcessor.process(tensorInputImage).buffer
        interpreter.run(
            processedImageBuffer,
            facialAttributesOutputArray
        )

        Log.d("HelloWorld", Arrays.toString(facialAttributesOutputArray[0]))

        return FacialAttributes(
            hasEyeglasses = facialAttributesOutputArray[0][0] > eyeglassesThreshold,
            hasMustache = facialAttributesOutputArray[0][1] > mustacheThreshold,
            hasBeard = facialAttributesOutputArray[0][2] < noBeardThreshold,
            isSmiling = facialAttributesOutputArray[0][3] > smilingThreshold,
        )
    }

}