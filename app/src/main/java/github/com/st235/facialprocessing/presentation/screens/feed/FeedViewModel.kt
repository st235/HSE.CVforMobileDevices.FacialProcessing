package github.com.st235.facialprocessing.presentation.screens.feed

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import github.com.st235.facialprocessing.domain.GalleryScanner
import github.com.st235.facialprocessing.domain.model.GalleryEntry
import github.com.st235.facialprocessing.domain.model.ProcessedGalleryEntry
import github.com.st235.facialprocessing.interactors.FeedInteractor
import github.com.st235.facialprocessing.utils.sample
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FeedViewModel(
    private val feedInteractor: FeedInteractor,
): ViewModel() {

    private companion object {
        const val TAG = "FeedViewModel"
    }

    private val _uiState = MutableStateFlow(
        FeedUiState.EMPTY
    )

    val uiState = _uiState.asStateFlow()

    private var scanningJob: Job? = null

    fun refreshProcessedData() {
        viewModelScope.launch {
            val processedImages = feedInteractor.getProcessedImages()
            val searchAttributes = feedInteractor.getSearchAttributes()
            val faceClusters = feedInteractor.getFaceClusters()

            _uiState.value = _uiState.value.copy(
                imagesWithFaces = processedImages.sample(5),
                searchAttributes = searchAttributes,
                faceClusters = faceClusters.sample(5),
            )
        }
    }

    fun startScanning() {
        scanningJob?.cancel()
        scanningJob = viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isPreparingToProcessing = true
            )

            feedInteractor.startScanning(ScanningCallback())
        }
    }

    private inner class ScanningCallback: GalleryScanner.ScanningCallback() {

        override fun onProcessingStart(unprocessedMediaCount: Int) {
            viewModelScope.launch {
                _uiState.value = _uiState.value.copy(
                    isPreparingToProcessing = false,
                    isProcessingImages = true,
                    processingProgress = 0f
                )
            }
        }

        override fun onProcessingProgress(
            processedGalleryEntry: ProcessedGalleryEntry,
            progress: Float
        ) {
            viewModelScope.launch {
                _uiState.value = _uiState.value.copy(
                    isProcessingImages = true,
                    processingProgress = progress
                )
            }
        }

        override fun onProcessingError(galleryEntry: GalleryEntry, progress: Float) {
            Log.w(TAG, "Processing error: $galleryEntry (${(progress*100).toInt()}%)")
        }

        override fun onProcessingFinished() {
            viewModelScope.launch {
                _uiState.value = _uiState.value.copy(
                    isProcessingImages = false,
                    processingProgress = 1f
                )
            }
        }

        override fun onClusteringStarted() {
            viewModelScope.launch {
                _uiState.value = _uiState.value.copy(
                    isClusteringImages = true,
                )
            }
        }

        override fun onClusteringFinished() {
            viewModelScope.launch {
                _uiState.value = _uiState.value.copy(
                    isClusteringImages = false,
                )
                refreshProcessedData()
            }
        }
    }
}
