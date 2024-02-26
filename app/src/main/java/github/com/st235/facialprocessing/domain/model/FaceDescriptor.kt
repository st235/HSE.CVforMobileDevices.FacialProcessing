package github.com.st235.facialprocessing.domain.model

data class FaceDescriptor(
    val region: Region,
    val age: Int,
    val gender: Gender,
    val emotion: Emotion,
    val attributes: Set<Attribute>,
    val embeddings: List<Float>,
) {
    data class Region(
        val left: Float,
        val top: Float,
        val width: Float,
        val height: Float,
    )

    enum class Gender {
        MALE,
        FEMALE,
    }

    enum class Emotion {
        ANGER,
        DISGUST,
        FEAR,
        HAPPINESS,
        NEUTRAL,
        SADNESS,
        SURPRISE,
        UNKNOWN,
    }

    enum class Attribute {
        GLASSES,
        MUSTACHE,
        BEARD,
        SMILE,
    }
}
