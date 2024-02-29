package github.com.st235.facialprocessing.presentation.screens.feed

import github.com.st235.facialprocessing.interactors.models.MediaEntry

data class FeedUiState(
    val imagesWithFaces: List<MediaEntry>,
) {
    companion object {
        val EMPTY = FeedUiState(
            imagesWithFaces = emptyList()
        )
    }
}
