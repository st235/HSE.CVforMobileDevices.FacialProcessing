package github.com.st235.facialprocessing.interactors

import github.com.st235.facialprocessing.data.FacesRepository
import github.com.st235.facialprocessing.interactors.models.FaceCluster
import github.com.st235.facialprocessing.interactors.utils.extractClusters
import github.com.st235.facialprocessing.utils.LocalUriLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ClustersInteractor(
    private val facesRepository: FacesRepository,
    private val localUriLoader: LocalUriLoader,
) {

    private val executionContext = Dispatchers.IO

    suspend fun getClusters(): List<FaceCluster> = withContext(executionContext) {
        extractClusters(facesRepository, localUriLoader)
    }

}