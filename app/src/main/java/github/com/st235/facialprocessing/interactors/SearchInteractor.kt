package github.com.st235.facialprocessing.interactors

import android.net.Uri
import github.com.st235.facialprocessing.data.FacesRepository
import github.com.st235.facialprocessing.data.db.FaceWithMediaFileEntity
import github.com.st235.facialprocessing.interactors.models.FaceCluster
import github.com.st235.facialprocessing.interactors.models.FaceSearchAttribute
import github.com.st235.facialprocessing.interactors.models.MediaEntry
import github.com.st235.facialprocessing.interactors.models.asMediaEntry
import github.com.st235.facialprocessing.interactors.utils.extractCluster
import github.com.st235.facialprocessing.utils.Assertion
import github.com.st235.facialprocessing.utils.LocalUriLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SearchInteractor(
    private val facesRepository: FacesRepository,
    private val localUriLoader: LocalUriLoader,
) {

    private val executionContext = Dispatchers.IO

    suspend fun findAllPhotos(): List<MediaEntry> = withContext(executionContext) {
        Assertion.assertOnWorkerThread()
        return@withContext facesRepository.getProcessedMediaFiles().map { it.asMediaEntry() }
    }

    suspend fun findPhotosByAttributes(attributeIds: List<Int>): List<MediaEntry> = withContext(executionContext) {
        Assertion.assertOnWorkerThread()

        val searchAttributes = attributeIds.map { FaceSearchAttribute.findSearchAttributesByTypeId(it) }
        val allProcessedFaces = facesRepository
            .getAllFaces()
            .filter { face -> searchAttributes.any { it.isApplicable(face) } }

        return@withContext allProcessedFaces.groupBy { MediaEntry(it.mediaId, Uri.parse(it.mediaUrl)) }.map { it.key }
    }

    suspend fun findPhotosByCluster(clusterId: Int): List<MediaEntry> = withContext(executionContext) {
        Assertion.assertOnWorkerThread()
        val faces = facesRepository.fetchFacesForCluster(clusterId)
        return@withContext faces.groupBy<FaceWithMediaFileEntity, MediaEntry> {
            MediaEntry(
                it.mediaId,
                Uri.parse(it.mediaUrl)
            )
        }.map<MediaEntry, List<FaceWithMediaFileEntity>, MediaEntry> { it.key }
    }

    suspend fun getSearchAttributesByIds(attributeIds: List<Int>): List<FaceSearchAttribute.Type> = withContext(executionContext) {
        Assertion.assertOnWorkerThread()
        return@withContext attributeIds.map { FaceSearchAttribute.findSearchAttributesByTypeId(it).type }
    }

    suspend fun getFaceCluster(clusterId: Int): FaceCluster = withContext(executionContext) {
        Assertion.assertOnWorkerThread()
        return@withContext extractCluster(clusterId, facesRepository, localUriLoader)
    }
}