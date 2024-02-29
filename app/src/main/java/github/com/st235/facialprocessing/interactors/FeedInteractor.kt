package github.com.st235.facialprocessing.interactors

import github.com.st235.facialprocessing.data.FacesRepository
import github.com.st235.facialprocessing.interactors.models.MediaEntry
import github.com.st235.facialprocessing.interactors.models.asMediaEntry
import github.com.st235.facialprocessing.utils.Assertion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FeedInteractor(
    private val facesRepository: FacesRepository,
) {

    private val executionContext = Dispatchers.IO

    suspend fun getProcessedImages(): List<MediaEntry> = withContext(executionContext) {
        Assertion.assertOnWorkerThread()
        return@withContext facesRepository.getMediaFilesWithFaces().map { it.asMediaEntry() }
    }

}