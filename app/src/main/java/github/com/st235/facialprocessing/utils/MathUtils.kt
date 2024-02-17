package github.com.st235.facialprocessing.utils

import kotlin.math.exp
import kotlin.math.max
import kotlin.math.min

fun sigmoid(value: Float): Float {
    return 1f / (1f + exp(-value))
}

fun clamp(value: Float, minValue: Float, maxValue: Float): Float {
    return max(minValue, min(maxValue, value))
}
