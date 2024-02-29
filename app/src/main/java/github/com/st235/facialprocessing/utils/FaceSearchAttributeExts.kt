package github.com.st235.facialprocessing.utils

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import github.com.st235.facialprocessing.R
import github.com.st235.facialprocessing.interactors.models.FaceSearchAttribute

@get:DrawableRes
val FaceSearchAttribute.Type.iconRes: Int
    get() {
        return when (this) {
            FaceSearchAttribute.Type.SEX_MALE -> R.drawable.ic_male_24
            FaceSearchAttribute.Type.SEX_FEMALE -> R.drawable.ic_female_24
            FaceSearchAttribute.Type.AGE_GROUP_0_25 -> R.drawable.ic_boy_24
            FaceSearchAttribute.Type.AGE_GROUP_25_45 -> R.drawable.ic_man_24
            FaceSearchAttribute.Type.AGE_GROUP_45_70 -> R.drawable.ic_man_24
            FaceSearchAttribute.Type.AGE_GROUP_70_AND_OLDER -> R.drawable.ic_elderly_24
            FaceSearchAttribute.Type.MUSTACHE -> R.drawable.ic_mustache_24
            FaceSearchAttribute.Type.EYEGLASSES -> R.drawable.ic_eyeglasses_24
            FaceSearchAttribute.Type.BEARD -> R.drawable.ic_cut_24
            FaceSearchAttribute.Type.SMILING -> R.drawable.ic_emoticon_24
            FaceSearchAttribute.Type.EMOTION_ANGER -> R.drawable.ic_sentiment_extremely_dissatisfied_24
            FaceSearchAttribute.Type.EMOTION_DISGUST -> R.drawable.ic_sentiment_stressed_24
            FaceSearchAttribute.Type.EMOTION_FEAR -> R.drawable.ic_sentiment_frustrated_24
            FaceSearchAttribute.Type.EMOTION_HAPPINESS -> R.drawable.ic_sentiment_satisfied_24
            FaceSearchAttribute.Type.EMOTION_NEUTRAL -> R.drawable.ic_sentiment_neutral_24
            FaceSearchAttribute.Type.EMOTION_SADNESS -> R.drawable.ic_sentiment_sad_24
            FaceSearchAttribute.Type.EMOTION_SURPRISE -> R.drawable.ic_sentiment_worried_24
        }
    }

@get:StringRes
val FaceSearchAttribute.Type.textRes: Int
    get() {
        return when (this) {
            FaceSearchAttribute.Type.SEX_MALE -> R.string.search_attribute_sex_male
            FaceSearchAttribute.Type.SEX_FEMALE -> R.string.search_attribute_sex_female
            FaceSearchAttribute.Type.AGE_GROUP_0_25 -> R.string.search_attribute_age_group_0_25
            FaceSearchAttribute.Type.AGE_GROUP_25_45 -> R.string.search_attribute_age_group_25_45
            FaceSearchAttribute.Type.AGE_GROUP_45_70 -> R.string.search_attribute_age_group_45_70
            FaceSearchAttribute.Type.AGE_GROUP_70_AND_OLDER -> R.string.search_attribute_age_group_70_plus
            FaceSearchAttribute.Type.MUSTACHE -> R.string.search_attribute_mustache
            FaceSearchAttribute.Type.EYEGLASSES -> R.string.search_attribute_eyeglasses
            FaceSearchAttribute.Type.BEARD -> R.string.search_attribute_beard
            FaceSearchAttribute.Type.SMILING -> R.string.search_attribute_smiling
            FaceSearchAttribute.Type.EMOTION_ANGER -> R.string.search_attribute_emotion_anger
            FaceSearchAttribute.Type.EMOTION_DISGUST -> R.string.search_attribute_emotion_disgust
            FaceSearchAttribute.Type.EMOTION_FEAR -> R.string.search_attribute_emotion_fear
            FaceSearchAttribute.Type.EMOTION_HAPPINESS -> R.string.search_attribute_emotion_happiness
            FaceSearchAttribute.Type.EMOTION_NEUTRAL -> R.string.search_attribute_emotion_neutral
            FaceSearchAttribute.Type.EMOTION_SADNESS -> R.string.search_attribute_emotion_sadness
            FaceSearchAttribute.Type.EMOTION_SURPRISE -> R.string.search_attribute_emotion_surprise
        }
    }
