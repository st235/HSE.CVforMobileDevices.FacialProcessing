package github.com.st235.facialprocessing.interactors.models

import github.com.st235.facialprocessing.data.db.FaceWithMediaFileEntity
import github.com.st235.facialprocessing.domain.model.FaceDescriptor

sealed class FaceSearchAttribute(
    val type: Type
) {
    enum class Type(val id: Int) {
        SEX_MALE(0),
        SEX_FEMALE(1),
        AGE_GROUP_0_25(2),
        AGE_GROUP_25_45(3),
        AGE_GROUP_45_70(4),
        AGE_GROUP_70_AND_OLDER(5),
        MUSTACHE(6),
        EYEGLASSES(7),
        BEARD(8),
        SMILING(9),
        EMOTION_ANGER(10),
        EMOTION_DISGUST(11),
        EMOTION_FEAR(12),
        EMOTION_HAPPINESS(13),
        EMOTION_NEUTRAL(14),
        EMOTION_SADNESS(15),
        EMOTION_SURPRISE(16);

        companion object {
            fun findById(id: Int): Type {
                return Type.entries.find { it.id == id } ?: throw IllegalArgumentException("Cannot find type associated with $id.")
            }
        }
    }

    abstract fun isApplicable(faceEntity: FaceWithMediaFileEntity): Boolean

    data object MaleSearchAttribute: FaceSearchAttribute(Type.SEX_MALE) {
        override fun isApplicable(faceEntity: FaceWithMediaFileEntity): Boolean {
            return FaceDescriptor.Gender.fromInt(faceEntity.gender) == FaceDescriptor.Gender.MALE
        }
    }

    data object FemaleSearchAttribute: FaceSearchAttribute(Type.SEX_FEMALE) {
        override fun isApplicable(faceEntity: FaceWithMediaFileEntity): Boolean {
            return FaceDescriptor.Gender.fromInt(faceEntity.gender) == FaceDescriptor.Gender.FEMALE
        }
    }

    data object AgeGroup025SearchAttribute: FaceSearchAttribute(Type.AGE_GROUP_0_25) {
        override fun isApplicable(faceEntity: FaceWithMediaFileEntity): Boolean {
            return faceEntity.age in 0..24
        }
    }

    data object AgeGroup2545SearchAttribute: FaceSearchAttribute(Type.AGE_GROUP_25_45) {
        override fun isApplicable(faceEntity: FaceWithMediaFileEntity): Boolean {
            return faceEntity.age in 25..44
        }
    }

    data object AgeGroup4570SearchAttribute: FaceSearchAttribute(Type.AGE_GROUP_45_70) {
        override fun isApplicable(faceEntity: FaceWithMediaFileEntity): Boolean {
            return faceEntity.age in 45..69
        }
    }

    data object AgeGroup70PlusSearchAttribute: FaceSearchAttribute(Type.AGE_GROUP_70_AND_OLDER) {
        override fun isApplicable(faceEntity: FaceWithMediaFileEntity): Boolean {
            return faceEntity.age >= 70
        }
    }

    data object MustacheSearchAttribute: FaceSearchAttribute(Type.MUSTACHE) {
        override fun isApplicable(faceEntity: FaceWithMediaFileEntity): Boolean {
            return faceEntity.hasMustache
        }
    }

    data object EyeglassesSearchAttribute: FaceSearchAttribute(Type.EYEGLASSES) {
        override fun isApplicable(faceEntity: FaceWithMediaFileEntity): Boolean {
            return faceEntity.hasGlasses
        }
    }

    data object BeardSearchAttribute: FaceSearchAttribute(Type.BEARD) {
        override fun isApplicable(faceEntity: FaceWithMediaFileEntity): Boolean {
            return faceEntity.hasBeard
        }
    }

    data object SmilingSearchAttribute: FaceSearchAttribute(Type.SMILING) {
        override fun isApplicable(faceEntity: FaceWithMediaFileEntity): Boolean {
            return faceEntity.isSmiling
        }
    }

    data object EmotionAngerSearchAttribute: FaceSearchAttribute(Type.EMOTION_ANGER) {
        override fun isApplicable(faceEntity: FaceWithMediaFileEntity): Boolean {
            return FaceDescriptor.Emotion.fromInt(faceEntity.emotion) == FaceDescriptor.Emotion.ANGER
        }
    }

    data object EmotionDisgustSearchAttribute: FaceSearchAttribute(Type.EMOTION_DISGUST) {
        override fun isApplicable(faceEntity: FaceWithMediaFileEntity): Boolean {
            return FaceDescriptor.Emotion.fromInt(faceEntity.emotion) == FaceDescriptor.Emotion.DISGUST
        }
    }

    data object EmotionFearSearchAttribute: FaceSearchAttribute(Type.EMOTION_FEAR) {
        override fun isApplicable(faceEntity: FaceWithMediaFileEntity): Boolean {
            return FaceDescriptor.Emotion.fromInt(faceEntity.emotion) == FaceDescriptor.Emotion.FEAR
        }
    }

    data object EmotionNeutralSearchAttribute: FaceSearchAttribute(Type.EMOTION_NEUTRAL) {
        override fun isApplicable(faceEntity: FaceWithMediaFileEntity): Boolean {
            return FaceDescriptor.Emotion.fromInt(faceEntity.emotion) == FaceDescriptor.Emotion.NEUTRAL
        }
    }

    data object EmotionHappinessSearchAttribute: FaceSearchAttribute(Type.EMOTION_HAPPINESS) {
        override fun isApplicable(faceEntity: FaceWithMediaFileEntity): Boolean {
            return FaceDescriptor.Emotion.fromInt(faceEntity.emotion) == FaceDescriptor.Emotion.HAPPINESS
        }
    }

    data object EmotionSadnessSearchAttribute: FaceSearchAttribute(Type.EMOTION_SADNESS) {
        override fun isApplicable(faceEntity: FaceWithMediaFileEntity): Boolean {
            return FaceDescriptor.Emotion.fromInt(faceEntity.emotion) == FaceDescriptor.Emotion.SADNESS
        }
    }

    data object EmotionSurpriseSearchAttribute: FaceSearchAttribute(Type.EMOTION_SURPRISE) {
        override fun isApplicable(faceEntity: FaceWithMediaFileEntity): Boolean {
            return FaceDescriptor.Emotion.fromInt(faceEntity.emotion) == FaceDescriptor.Emotion.SURPRISE
        }
    }

    companion object {
        val SEARCH_ATTRIBUTES = arrayOf(
            MaleSearchAttribute,
            FemaleSearchAttribute,
            AgeGroup025SearchAttribute,
            AgeGroup2545SearchAttribute,
            AgeGroup4570SearchAttribute,
            AgeGroup70PlusSearchAttribute,
            MustacheSearchAttribute,
            EyeglassesSearchAttribute,
            BeardSearchAttribute,
            SmilingSearchAttribute,
            EmotionAngerSearchAttribute,
            EmotionSurpriseSearchAttribute,
            EmotionFearSearchAttribute,
            EmotionDisgustSearchAttribute,
            EmotionHappinessSearchAttribute,
            EmotionNeutralSearchAttribute,
            EmotionSadnessSearchAttribute,
        )

        fun findSearchAttributesByTypeId(typeId: Int): FaceSearchAttribute {
            return findSearchAttributesByType(type = Type.findById(typeId))
        }

        fun findSearchAttributesByType(type: Type): FaceSearchAttribute {
            return SEARCH_ATTRIBUTES.find { it.type == type } ?: throw IllegalArgumentException("Cannot find type $type")
        }
    }
}

fun FaceWithMediaFileEntity.extractFaceSearchAttributes(): Set<FaceSearchAttribute.Type> {
    val attributes = mutableSetOf<FaceSearchAttribute.Type>()

    for (searchAttribute in FaceSearchAttribute.SEARCH_ATTRIBUTES) {
        if (searchAttribute.isApplicable(this)) {
            attributes.add(searchAttribute.type)
        }
    }

    return attributes
}

fun List<FaceWithMediaFileEntity>.extractImageFaceAttributes(): Set<FaceSearchAttribute.Type> {
    val attributes = mutableSetOf<FaceSearchAttribute.Type>()

    for (face in this) {
        for (searchAttribute in FaceSearchAttribute.SEARCH_ATTRIBUTES) {
            if (searchAttribute.isApplicable(face)) {
                attributes.add(searchAttribute.type)
            }

            if (attributes.size == FaceSearchAttribute.SEARCH_ATTRIBUTES.size) {
                break
            }
        }
    }

    return attributes
}
