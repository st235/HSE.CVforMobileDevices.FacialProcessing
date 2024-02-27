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
        FEMALE;

        companion object {
            fun Gender.toInt(): Int {
                return when(this) {
                    FEMALE -> 0
                    MALE -> 1
                }
            }

            fun fromInt(raw: Int): Gender {
                return when(raw) {
                    0 -> FEMALE
                    1 -> MALE
                    else -> throw IllegalArgumentException("Unknown value $raw")
                }
            }
        }
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
