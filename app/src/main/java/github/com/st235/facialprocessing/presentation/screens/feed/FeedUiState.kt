package github.com.st235.facialprocessing.presentation.screens.feed

import github.com.st235.facialprocessing.interactors.models.FaceSearchAttribute
import github.com.st235.facialprocessing.interactors.models.MediaEntry

data class FeedUiState(
    val isProcessingImages: Boolean,
    val processingProgress: Float,
    val isClusteringImages: Boolean,
    val imagesWithFaces: List<MediaEntry>,
    val searchAttributes: Set<FaceSearchAttribute.Type>,
) {
    companion object {
        val EMPTY = FeedUiState(
            isProcessingImages = false,
            processingProgress = 0f,
            isClusteringImages = false,
            imagesWithFaces = emptyList(),
            searchAttributes = emptySet(),
        )
    }
}
