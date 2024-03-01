package github.com.st235.facialprocessing.presentation.screens.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import github.com.st235.facialprocessing.interactors.DetailsInteractor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DetailsViewModel(
    private val detailsInteractor: DetailsInteractor,
): ViewModel() {

    private val _uiState = MutableStateFlow(DetailsUiState.EMPTY)

    val uiState = _uiState.asStateFlow()

    fun loadState(mediaId: Int) {
        viewModelScope.launch {
            val bitmap = detailsInteractor.findMediaById(mediaId)
            val (faceDescriptors, searchAttributes) = detailsInteractor.findFacesByMediaId(mediaId)

            _uiState.value = _uiState.value.copy(
                bitmap = bitmap,
                faceDescriptors = faceDescriptors,
                searchAttributes = searchAttributes,
            )
        }
    }

}