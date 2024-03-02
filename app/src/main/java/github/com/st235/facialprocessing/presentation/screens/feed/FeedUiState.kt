package github.com.st235.facialprocessing.presentation.screens.feed

import github.com.st235.facialprocessing.interactors.models.FaceCluster
import github.com.st235.facialprocessing.interactors.models.FaceSearchAttribute
import github.com.st235.facialprocessing.interactors.models.MediaEntry

data class FeedUiState(
    val status: Status,
    val photosToProcessCount: Int,
    val processingProgress: Float,
    val imagesWithFaces: List<MediaEntry>,
    val searchAttributes: Set<FaceSearchAttribute.Type>,
    val faceClusters: List<FaceCluster>,
) {
    enum class Status {
        PREPARING_TO_PROCESSING,
        PROCESSING_IMAGES,
        CLUSTERING,
        LOADING_DATA,
        READY,
    }

    companion object {
        val EMPTY = FeedUiState(
            status = Status.READY,
            photosToProcessCount = 0,
            processingProgress = 0f,
            imagesWithFaces = emptyList(),
            searchAttributes = emptySet(),
            faceClusters = emptyList(),
        )
    }
}

val FeedUiState.Status.canShowScanButton: Boolean
    get() {
        return when(this) {
            FeedUiState.Status.PREPARING_TO_PROCESSING -> false
            FeedUiState.Status.PROCESSING_IMAGES -> false
            FeedUiState.Status.CLUSTERING -> false
            FeedUiState.Status.LOADING_DATA -> false
            FeedUiState.Status.READY -> true
        }
    }
