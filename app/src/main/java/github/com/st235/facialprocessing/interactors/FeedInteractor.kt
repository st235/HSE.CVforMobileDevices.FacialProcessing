package github.com.st235.facialprocessing.interactors

import github.com.st235.facialprocessing.data.FacesRepository
import github.com.st235.facialprocessing.domain.GalleryScanner
import github.com.st235.facialprocessing.interactors.models.FaceSearchAttribute
import github.com.st235.facialprocessing.interactors.models.MediaEntry
import github.com.st235.facialprocessing.interactors.models.asMediaEntry
import github.com.st235.facialprocessing.interactors.models.asSearchAttributes
import github.com.st235.facialprocessing.utils.Assertion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FeedInteractor(
    private val facesRepository: FacesRepository,
    private val galleryScanner: GalleryScanner,
) {

    private val executionContext = Dispatchers.IO

    suspend fun startScanning(callback: GalleryScanner.ScanningCallback) = withContext(executionContext) {
        Assertion.assertOnWorkerThread()
        galleryScanner.start(callback)
    }

    suspend fun getProcessedImages(): List<MediaEntry> = withContext(executionContext) {
        Assertion.assertOnWorkerThread()
        return@withContext facesRepository.getMediaFilesWithFaces().map { it.asMediaEntry() }
    }

    suspend fun getSearchAttributes(): Set<FaceSearchAttribute.Type> = withContext(executionContext) {
        Assertion.assertOnWorkerThread()
        val faces = facesRepository.getAllFaces()
        return@withContext faces.asSearchAttributes()
    }

}