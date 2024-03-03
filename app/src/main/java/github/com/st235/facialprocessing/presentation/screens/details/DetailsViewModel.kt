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

    fun loadState(mediaId: Long, clusterId: Int?) {
        viewModelScope.launch {
            _uiState.value = DetailsUiState.EMPTY.copy(
                isLoading = true
            )

            val bitmap = detailsInteractor.findMediaById(mediaId)
            val (faceDescriptors, imageSearchAttributes) = detailsInteractor.findFacesByMediaId(mediaId)
            val allFacesInMediaFileBelongToTheSameCluster = if (clusterId != null) {
                detailsInteractor.getAllFacesInImageInTheSameCluster(mediaId, clusterId)
            } else {
                emptyList()
            }

            val faceId = when {
                faceDescriptors.size == 1 -> faceDescriptors.keys.first()
                allFacesInMediaFileBelongToTheSameCluster.size == 1 -> allFacesInMediaFileBelongToTheSameCluster.first()
                else -> null
            }

            val faceCluster = if (faceId != null) {
                detailsInteractor.getFaceClusterByFaceId(faceId)
            } else {
                null
            }

            _uiState.value = _uiState.value.copy(
                bitmap = bitmap,
                selectedFaceId = faceId,
                selectedFaceCluster = faceCluster,
                faceDescriptors = faceDescriptors,
                searchAttributes = imageSearchAttributes,
            )
        }
    }

    fun selectFace(faceId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                selectedFaceId = faceId,
            )

            val faceCluster = detailsInteractor.getFaceClusterByFaceId(faceId)

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                selectedFaceId = faceId,
                selectedFaceCluster = faceCluster,
            )
        }
    }

}