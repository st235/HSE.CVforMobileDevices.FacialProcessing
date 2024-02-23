package github.com.st235.facialprocessing.domain

import android.graphics.Bitmap
import github.com.st235.facialprocessing.R
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp

class AgeExtractor(
    interpreterFactory: InterpreterFactory
) {

    private companion object {
        const val INPUT_IMAGE_SIZE = 200
        const val SCALE = 116
    }

    private val inputImageProcessor =
        ImageProcessor.Builder()
            .add(ResizeOp(INPUT_IMAGE_SIZE, INPUT_IMAGE_SIZE, ResizeOp.ResizeMethod.BILINEAR))
            .add(NormalizeOp(0f, 255f))
            .build()

    private val interpreter: Interpreter = interpreterFactory.create(R.raw.model_age)

    fun predict(image: Bitmap): Float {
        val tensorInputImage = TensorImage.fromBitmap(image)
        val ageOutputArray = Array(1){ FloatArray(1) }
        val processedImageBuffer = inputImageProcessor.process(tensorInputImage).buffer
        interpreter.run(
            processedImageBuffer,
            ageOutputArray
        )
        return ageOutputArray[0][0] * SCALE
    }

}