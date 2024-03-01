package github.com.st235.facialprocessing.presentation.screens.search

import github.com.st235.facialprocessing.interactors.models.MediaEntry

data class SearchUiState(
    val person: Int?,
    val images: List<MediaEntry>,
) {
    companion object {
        val EMPTY = SearchUiState(
            person = null,
            images = emptyList()
        )
    }
}
