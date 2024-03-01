package github.com.st235.facialprocessing.presentation.screens.clusters

import github.com.st235.facialprocessing.interactors.models.FaceCluster

data class ClustersUiState(
    val clusters: List<FaceCluster>
) {
    companion object {
        val EMPTY = ClustersUiState(
            clusters = emptyList()
        )
    }
}
