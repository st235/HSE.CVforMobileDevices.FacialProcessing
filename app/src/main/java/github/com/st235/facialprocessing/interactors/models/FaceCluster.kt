package github.com.st235.facialprocessing.interactors.models

import android.graphics.Bitmap

data class FaceCluster(
    val id: Int,
    val sampleFace: Bitmap,
)
