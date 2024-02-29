package github.com.st235.facialprocessing.presentation.screens.feed

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import github.com.st235.facialprocessing.interactors.FeedInteractor
import github.com.st235.facialprocessing.utils.MediaRetriever
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

            _uiState.value = _uiState.value.copy(imagesWithFaces = processedImages)
        }
    }

}
