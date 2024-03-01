package github.com.st235.facialprocessing.presentation.screens.clusters

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import github.com.st235.facialprocessing.interactors.ClustersInteractor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ClustersViewModel(
    private val clustersInteractor: ClustersInteractor
): ViewModel() {

    private val _uiState = MutableStateFlow(ClustersUiState.EMPTY)

    val uiState = _uiState.asStateFlow()

    fun loadState() {
        viewModelScope.launch {
            val clusters = clustersInteractor.getClusters()

            _uiState.value = _uiState.value.copy(
                clusters = clusters,
            )
        }
    }
}
