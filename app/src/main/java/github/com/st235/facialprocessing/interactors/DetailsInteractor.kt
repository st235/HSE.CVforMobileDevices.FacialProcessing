package github.com.st235.facialprocessing.interactors

import android.graphics.Bitmap
import android.net.Uri
import github.com.st235.facialprocessing.data.FacesRepository
import github.com.st235.facialprocessing.domain.model.FaceDescriptor
import github.com.st235.facialprocessing.domain.model.asFaceDescriptor
import github.com.st235.facialprocessing.interactors.models.FaceSearchAttribute
import github.com.st235.facialprocessing.interactors.models.asSearchAttributes
import github.com.st235.facialprocessing.utils.Assertion
import github.com.st235.facialprocessing.utils.LocalUriLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DetailsInteractor(
    private val facesRepository: FacesRepository,
    private val localUriLoader: LocalUriLoader,
) {

    private val executionContext = Dispatchers.IO

    suspend fun findMediaById(mediaId: Int): Bitmap? = withContext(executionContext) {
        Assertion.assertOnWorkerThread()

        val mediaEntity = facesRepository.getMediaFileById(mediaId)
        return@withContext localUriLoader.load(Uri.parse(mediaEntity.mediaUri))
    }

    suspend fun findFacesByMediaId(mediaId: Int): Pair<List<FaceDescriptor>, Set<FaceSearchAttribute.Type>> = withContext(executionContext) {
        Assertion.assertOnWorkerThread()

        val faceEntities = facesRepository.getAllFacesAtMediaFile(mediaId)
        return@withContext  faceEntities.map { it.asFaceDescriptor() } to faceEntities.asSearchAttributes()
    }
}
