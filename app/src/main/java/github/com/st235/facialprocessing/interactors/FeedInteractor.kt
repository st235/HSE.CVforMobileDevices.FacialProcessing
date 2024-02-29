package github.com.st235.facialprocessing.interactors

import github.com.st235.facialprocessing.data.FacesRepository
import github.com.st235.facialprocessing.interactors.models.FaceSearchAttribute
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

    suspend fun getSearchAttributes(): Set<FaceSearchAttribute.Type> = withContext(executionContext) {
        val faces = facesRepository.getAllFaces()

        val searchAttributes = mutableSetOf<FaceSearchAttribute.Type>()
        for (face in faces) {
            for (searchAttribute in FaceSearchAttribute.SEARCH_ATTRIBUTES) {
                if (searchAttribute.isApplicable(face)) {
                    searchAttributes.add(searchAttribute.type)
                }

                if (searchAttributes.size == FaceSearchAttribute.SEARCH_ATTRIBUTES.size) {
                    break
                }
            }
        }

        return@withContext searchAttributes
    }

}