package github.com.st235.facialprocessing.interactors.models

import github.com.st235.facialprocessing.data.db.FaceWithMediaFileEntity
import github.com.st235.facialprocessing.domain.model.FaceDescriptor

sealed class FaceSearchAttribute(
    val type: Type
) {
    enum class Type {
        SEX_MALE,
        SEX_FEMALE,
        AGE_GROUP_0_25,
        AGE_GROUP_25_45,
        AGE_GROUP_45_70,
        AGE_GROUP_70_AND_OLDER,
        MUSTACHE,
        EYEGLASSES,
        BEARD,
        SMILING,
        EMOTION_ANGER,
        EMOTION_DISGUST,
        EMOTION_FEAR,
        EMOTION_HAPPINESS,
        EMOTION_NEUTRAL,
        EMOTION_SADNESS,
        EMOTION_SURPRISE,
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

        fun findSearchAttributesByType(type: Type): FaceSearchAttribute {
            return SEARCH_ATTRIBUTES.find { it.type == type } ?: throw IllegalArgumentException("Cannot find type $type")
        }
    }
}
