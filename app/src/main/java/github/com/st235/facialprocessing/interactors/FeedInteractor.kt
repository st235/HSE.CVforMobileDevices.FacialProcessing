package github.com.st235.facialprocessing.interactors

import android.graphics.Bitmap
import android.net.Uri
import github.com.st235.facialprocessing.data.FacesRepository
import github.com.st235.facialprocessing.domain.GalleryScanner
import github.com.st235.facialprocessing.domain.model.asFaceDescriptor
import github.com.st235.facialprocessing.interactors.models.FaceCluster
import github.com.st235.facialprocessing.interactors.models.FaceSearchAttribute
import github.com.st235.facialprocessing.interactors.models.MediaEntry
import github.com.st235.facialprocessing.interactors.models.asMediaEntry
import github.com.st235.facialprocessing.interactors.models.asSearchAttributes
import github.com.st235.facialprocessing.utils.Assertion
import github.com.st235.facialprocessing.utils.LocalUriLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FeedInteractor(
    private val facesRepository: FacesRepository,
    private val galleryScanner: GalleryScanner,
    private val localUriLoader: LocalUriLoader,
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

    suspend fun getFaceClusters(): List<FaceCluster> = withContext(executionContext) {
        val faceClusters = mutableListOf<FaceCluster>()
        val clusters = facesRepository.getClusterToFacesLookup()

        for (cluster in clusters.entries) {
            val clusterId = cluster.key
            val randomFaceId = cluster.value.random()
            val randomFaceEntity = facesRepository.getFaceById(randomFaceId)

            val mediaFile = localUriLoader.load(Uri.parse(randomFaceEntity.mediaUrl))

            if (mediaFile == null) {
                throw IllegalStateException("Media file should never be null.")
            }

            val faceBitmap = Bitmap.createBitmap(
                mediaFile,
                (randomFaceEntity.left * mediaFile.width).toInt(), (randomFaceEntity.top * mediaFile.height).toInt(),
                (randomFaceEntity.width * mediaFile.width).toInt(), (randomFaceEntity.height * mediaFile.height).toInt(),
            )

            faceClusters.add(
                FaceCluster(
                    id = clusterId,
                    sampleFace = faceBitmap
                )
            )
        }

        return@withContext faceClusters
    }
}