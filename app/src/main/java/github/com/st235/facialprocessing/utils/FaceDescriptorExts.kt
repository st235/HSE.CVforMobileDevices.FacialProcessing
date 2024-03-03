package github.com.st235.facialprocessing.utils

import androidx.annotation.StringRes
import github.com.st235.facialprocessing.R
import github.com.st235.facialprocessing.domain.model.FaceDescriptor

@get:StringRes
val FaceDescriptor.Gender.textRes: Int
    get() {
        return when (this) {
            FaceDescriptor.Gender.MALE -> R.string.search_attribute_sex_male
            FaceDescriptor.Gender.FEMALE -> R.string.search_attribute_sex_female
        }
    }

@get:StringRes
val FaceDescriptor.Emotion.textRes: Int
    get() {
        return when (this) {
            FaceDescriptor.Emotion.ANGER -> R.string.search_attribute_emotion_anger
            FaceDescriptor.Emotion.DISGUST -> R.string.search_attribute_emotion_disgust
            FaceDescriptor.Emotion.FEAR -> R.string.search_attribute_emotion_fear
            FaceDescriptor.Emotion.HAPPINESS -> R.string.search_attribute_emotion_happiness
            FaceDescriptor.Emotion.NEUTRAL -> R.string.search_attribute_emotion_neutral
            FaceDescriptor.Emotion.SADNESS -> R.string.search_attribute_emotion_sadness
            FaceDescriptor.Emotion.SURPRISE -> R.string.search_attribute_emotion_surprise
            FaceDescriptor.Emotion.UNKNOWN -> R.string.search_attribute_emotion_unknown
        }
    }

@get:StringRes
val FaceDescriptor.Attribute.textRes: Int
    get() {
        return when (this) {
            FaceDescriptor.Attribute.MUSTACHE -> R.string.search_attribute_mustache
            FaceDescriptor.Attribute.GLASSES  -> R.string.search_attribute_eyeglasses
            FaceDescriptor.Attribute.BEARD -> R.string.search_attribute_beard
            FaceDescriptor.Attribute.SMILE -> R.string.search_attribute_smiling
        }
    }
