package github.com.st235.facialprocessing.presentation.screens.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import github.com.st235.facialprocessing.interactors.FeedInteractor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FeedViewModel(
    private val feedInteractor: FeedInteractor,
): ViewModel() {

    private val _uiState = MutableStateFlow(
        FeedUiState.EMPTY
    )

    val uiState = _uiState.asStateFlow()

    fun loadState() {
        viewModelScope.launch {
            val processedImages = feedInteractor.getProcessedImages()
            val searchAttributes = feedInteractor.getSearchAttributes()

            _uiState.value = _uiState.value.copy(
                imagesWithFaces = processedImages,
                searchAttributes = searchAttributes,
            )
        }
    }

}
