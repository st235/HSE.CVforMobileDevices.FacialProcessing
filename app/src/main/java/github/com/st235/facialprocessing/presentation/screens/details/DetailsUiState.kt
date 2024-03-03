package github.com.st235.facialprocessing.presentation.screens.details

import android.graphics.Bitmap
import github.com.st235.facialprocessing.domain.model.FaceDescriptor
import github.com.st235.facialprocessing.interactors.models.FaceCluster
import github.com.st235.facialprocessing.interactors.models.FaceSearchAttribute

data class DetailsUiState(
    val isLoading: Boolean,
    val bitmap: Bitmap?,
    val selectedFaceId: Int?,
    val selectedFaceCluster: FaceCluster?,
    val faceDescriptors: Map<Int, Pair<FaceDescriptor, Set<FaceSearchAttribute.Type>>>,
    val searchAttributes: Set<FaceSearchAttribute.Type>,
) {
    companion object {
        val EMPTY = DetailsUiState(
            isLoading = false,
            selectedFaceId = null,
            bitmap = null,
            selectedFaceCluster = null,
            faceDescriptors = emptyMap(),
            searchAttributes = emptySet(),
        )
    }
}
