package github.com.st235.facialprocessing.domain.faces

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
class GenderExtractor(
    interpreterFactory: InterpreterFactory
) {

    private companion object {
        const val INPUT_IMAGE_SIZE = 128
    }

    enum class Gender {
        MALE,
        FEMALE,
    }

    private val inputImageProcessor =
        ImageProcessor.Builder()
            .add(ResizeOp(INPUT_IMAGE_SIZE, INPUT_IMAGE_SIZE, ResizeOp.ResizeMethod.BILINEAR))
            .add(NormalizeOp(0f, 255f))
            .build()

    private val interpreter: Interpreter = interpreterFactory.create(R.raw.model_gender)

    fun predict(image: Bitmap): Gender {
        val tensorInputImage = TensorImage.fromBitmap(image)
        val ageOutputArray = Array(1){ FloatArray(2) }
        val processedImageBuffer = inputImageProcessor.process(tensorInputImage).buffer
        interpreter.run(
            processedImageBuffer,
            ageOutputArray
        )

        val rawGender = ageOutputArray[0]
        return if (rawGender[0] > rawGender[1]) {
            Gender.MALE
        } else {
            Gender.FEMALE
        }
    }

}