package github.com.st235.facialprocessing.presentation.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import github.com.st235.facialprocessing.interactors.SearchInteractor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchViewModel(
    private val searchInteractor: SearchInteractor
): ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState.EMPTY)

    val uiState = _uiState.asStateFlow()

    fun loadState(personId: Int?, searchAttributeIds: List<Int>) {
        viewModelScope.launch {
            val images = if (personId == null && searchAttributeIds.isEmpty()) {
                searchInteractor.findAllPhotos()
            } else if (personId != null) {
                searchInteractor.findPhotosByCluster(personId)
            } else {
                searchInteractor.findPhotosByAttributes(searchAttributeIds)
            }

            _uiState.value = _uiState.value.copy(
                person = personId,
                images = images,
            )
        }
    }
}