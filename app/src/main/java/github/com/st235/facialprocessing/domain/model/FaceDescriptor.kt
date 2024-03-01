package github.com.st235.facialprocessing.domain.model

import github.com.st235.facialprocessing.data.db.FaceWithMediaFileEntity

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
                return when (this) {
                    FEMALE -> 0
                    MALE -> 1
                }
            }

            fun fromInt(raw: Int): Gender {
                return when (raw) {
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
        UNKNOWN;

        companion object {
            fun Emotion.toInt(): Int {
                return when (this) {
                    ANGER -> 0
                    DISGUST -> 1
                    FEAR -> 2
                    HAPPINESS -> 3
                    NEUTRAL -> 4
                    SADNESS -> 5
                    SURPRISE -> 6
                    UNKNOWN -> 7
                }
            }

            fun fromInt(raw: Int): Emotion {
                return when (raw) {
                    0 -> ANGER
                    1 -> DISGUST
                    2 -> FEAR
                    3 -> HAPPINESS
                    4 -> NEUTRAL
                    5 -> SADNESS
                    6 -> SURPRISE
                    7 -> UNKNOWN
                    else -> throw IllegalArgumentException("Unknown value $raw")
                }
            }
        }
    }

    enum class Attribute {
        GLASSES,
        MUSTACHE,
        BEARD,
        SMILE,
    }
}

fun FaceWithMediaFileEntity.asFaceDescriptor(): FaceDescriptor {
    val attributes = mutableSetOf<FaceDescriptor.Attribute>()

    if (hasMustache) {
        attributes.add(FaceDescriptor.Attribute.MUSTACHE)
    }
    if (hasBeard) {
        attributes.add(FaceDescriptor.Attribute.BEARD)
    }
    if (hasGlasses) {
        attributes.add(FaceDescriptor.Attribute.GLASSES)
    }
    if (isSmiling) {
        attributes.add(FaceDescriptor.Attribute.SMILE)
    }

    return FaceDescriptor(
        region = FaceDescriptor.Region(left, top, width, height),
        age = age,
        gender = FaceDescriptor.Gender.fromInt(gender),
        emotion = FaceDescriptor.Emotion.fromInt(emotion),
        attributes = attributes,
        embeddings = embeddings,
    )
}
