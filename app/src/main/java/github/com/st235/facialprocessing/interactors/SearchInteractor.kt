package github.com.st235.facialprocessing.interactors

import android.net.Uri
import github.com.st235.facialprocessing.data.FacesRepository
import github.com.st235.facialprocessing.domain.model.asFaceDescriptor
import github.com.st235.facialprocessing.interactors.models.FaceSearchAttribute
import github.com.st235.facialprocessing.interactors.models.MediaEntry
import github.com.st235.facialprocessing.interactors.models.asMediaEntry
import github.com.st235.facialprocessing.utils.Assertion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SearchInteractor(
    private val facesRepository: FacesRepository,
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
        TODO()
    }
}