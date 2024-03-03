package github.com.st235.facialprocessing.interactors

import android.graphics.Bitmap
import android.net.Uri
import github.com.st235.facialprocessing.data.FacesRepository
import github.com.st235.facialprocessing.domain.model.FaceDescriptor
import github.com.st235.facialprocessing.domain.model.asFaceDescriptor
import github.com.st235.facialprocessing.interactors.models.FaceCluster
import github.com.st235.facialprocessing.interactors.models.FaceSearchAttribute
import github.com.st235.facialprocessing.interactors.models.extractFaceSearchAttributes
import github.com.st235.facialprocessing.interactors.models.extractImageFaceAttributes
import github.com.st235.facialprocessing.interactors.utils.extractCluster
import github.com.st235.facialprocessing.utils.Assertion
import github.com.st235.facialprocessing.utils.LocalUriLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DetailsInteractor(
    private val facesRepository: FacesRepository,
    private val localUriLoader: LocalUriLoader,
) {

    private val executionContext = Dispatchers.IO

    suspend fun findMediaById(mediaId: Long): Bitmap? = withContext(executionContext) {
        Assertion.assertOnWorkerThread()

        val mediaEntity = facesRepository.getMediaFileById(mediaId)
        return@withContext localUriLoader.load(Uri.parse(mediaEntity.mediaUri))
    }

    suspend fun findFacesByMediaId(mediaId: Long): Pair<Map<Int, Pair<FaceDescriptor, Set<FaceSearchAttribute.Type>>>, Set<FaceSearchAttribute.Type>> = withContext(executionContext) {
        Assertion.assertOnWorkerThread()
        val faceEntities = facesRepository.getAllFacesAtMediaFile(mediaId)
        return@withContext  faceEntities.associate { it.id to (it.asFaceDescriptor() to it.extractFaceSearchAttributes()) } to faceEntities.extractImageFaceAttributes()
    }

    suspend fun getFaceClusterByFaceId(faceId: Int): FaceCluster = withContext(executionContext) {
        Assertion.assertOnWorkerThread()
       val clusterId = facesRepository.getClusterIdByFaceId(faceId)
        return@withContext extractCluster(clusterId, facesRepository, localUriLoader)
    }

    suspend fun getAllFacesInImageInTheSameCluster(
        mediaId: Long,
        clusterId: Int
    ): List<Int> = withContext(executionContext) {
        Assertion.assertOnWorkerThread()
        val facesInCluster = facesRepository.getClusterToFacesLookup().getValue(clusterId).toHashSet()
        val facesInMedia = facesRepository.getAllFacesAtMediaFile(mediaId).map { it.id }.toHashSet()

        val result = mutableListOf<Int>()
        for (face in facesInCluster) {
            if (facesInMedia.contains(face)) {
                result.add(face)
            }
        }

        return@withContext result
    }
}
