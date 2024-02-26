package github.com.st235.facialprocessing.domain.faces

import android.graphics.Bitmap
import androidx.annotation.WorkerThread
import github.com.st235.facialprocessing.utils.tflite.InterpreterFactory
import kotlin.math.roundToInt

@WorkerThread
class FaceProcessor(
    interpreterFactory: InterpreterFactory,
) {

    private val faceDetector = FaceDetector(interpreterFactory)
    private val ageExtractor = AgeExtractor(interpreterFactory)
    private val genderExtractor = GenderExtractor(interpreterFactory)
    private val emotionExtractor = EmotionExtractor(interpreterFactory)
    private val attributesExtractor = FacialAttributesExtractor(interpreterFactory)
    private val embeddingsExtractor = FaceEmbeddingsExtractor(interpreterFactory)

    fun detect(image: Bitmap): List<FaceDescriptor> {
        val descriptors = mutableListOf<FaceDescriptor>()
        val faces = faceDetector.detect(image)

        for (face in faces) {
            val rescaledFace = face.rescaleToImage(image)
            val faceBitmap = Bitmap.createBitmap(
                image,
                rescaledFace.xMin.toInt(), rescaledFace.yMin.toInt(),
                rescaledFace.width.toInt(), rescaledFace.height.toInt()
            )

            val age = ageExtractor.predict(faceBitmap)
            val gender = genderExtractor.predict(faceBitmap)
            val emotion = emotionExtractor.predict(faceBitmap)
            val attributes = attributesExtractor.predict(faceBitmap)
            val embeddings = embeddingsExtractor.predict(faceBitmap)

            val descriptor = FaceDescriptor(
                region = FaceDescriptor.Region(
                    left = face.xMin,
                    top = face.yMin,
                    width = face.width,
                    height = face.height,
                ),
                age = age.roundToInt(),
                gender = gender.asFaceDescriptorGender(),
                emotion = emotion.asFaceDescriptorEmotion(),
                attributes = attributes.asFaceDescriptorAttributes(),
                embeddings = embeddings.map { it }
            )

            descriptors.add(descriptor)
        }

        return descriptors
    }

    private fun FaceDetector.Box.rescaleToImage(image: Bitmap): FaceDetector.Box {
        return FaceDetector.Box(
            xMin = xMin * image.width,
            yMin = yMin * image.height,
            xMax = xMax * image.width,
            yMax = yMax * image.height,
            score = score,
        )
    }

    private fun GenderExtractor.Gender.asFaceDescriptorGender(): FaceDescriptor.Gender {
        return when (this) {
            GenderExtractor.Gender.MALE -> FaceDescriptor.Gender.MALE
            GenderExtractor.Gender.FEMALE -> FaceDescriptor.Gender.FEMALE
        }
    }

    private fun EmotionExtractor.Emotion.asFaceDescriptorEmotion(): FaceDescriptor.Emotion {
        return when (this) {
            EmotionExtractor.Emotion.ANGER -> FaceDescriptor.Emotion.ANGER
            EmotionExtractor.Emotion.FEAR -> FaceDescriptor.Emotion.FEAR
            EmotionExtractor.Emotion.DISGUST -> FaceDescriptor.Emotion.DISGUST
            EmotionExtractor.Emotion.HAPPINESS -> FaceDescriptor.Emotion.HAPPINESS
            EmotionExtractor.Emotion.NEUTRAL -> FaceDescriptor.Emotion.NEUTRAL
            EmotionExtractor.Emotion.SADNESS -> FaceDescriptor.Emotion.SADNESS
            EmotionExtractor.Emotion.SURPRISE -> FaceDescriptor.Emotion.SURPRISE
            EmotionExtractor.Emotion.UNKNOWN -> FaceDescriptor.Emotion.UNKNOWN
        }
    }

    private fun FacialAttributesExtractor.FacialAttributes.asFaceDescriptorAttributes(): Set<FaceDescriptor.Attribute> {
        val attributes = mutableSetOf<FaceDescriptor.Attribute>()

        if (hasBeard) {
            attributes.add(FaceDescriptor.Attribute.BEARD)
        }

        if (hasEyeglasses) {
            attributes.add(FaceDescriptor.Attribute.GLASSES)
        }

        if (hasMustache) {
            attributes.add(FaceDescriptor.Attribute.MUSTACHE)
        }

        if (isSmiling) {
            attributes.add(FaceDescriptor.Attribute.SMILE)
        }

        return attributes
    }
}