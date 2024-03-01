package github.com.st235.facialprocessing.presentation.screens.details

import android.graphics.Bitmap
import github.com.st235.facialprocessing.domain.model.FaceDescriptor
import github.com.st235.facialprocessing.interactors.models.FaceSearchAttribute

data class DetailsUiState(
    val bitmap: Bitmap?,
    val faceDescriptors: List<FaceDescriptor>,
    val searchAttributes: Set<FaceSearchAttribute.Type>,
) {
    companion object {
        val EMPTY = DetailsUiState(
            bitmap = null,
            faceDescriptors = emptyList(),
            searchAttributes = emptySet(),
        )
    }
}
