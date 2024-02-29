package github.com.st235.facialprocessing.domain

import github.com.st235.facialprocessing.data.FacesRepository
import github.com.st235.facialprocessing.data.db.FaceWithMediaFileEntity
import github.com.st235.facialprocessing.domain.clustering.Clusterer
import github.com.st235.facialprocessing.domain.model.GalleryEntry
import github.com.st235.facialprocessing.domain.model.ProcessedGalleryEntry

class ClusterProcessor(
    private val facesRepository: FacesRepository,
    private val clusterer: Clusterer<FaceWithMediaFileEntity>
) {

    fun cluster() {
        val allImages = facesRepository.getAllFaces()
        val clusters = clusterer.cluster(allImages)

        facesRepository.updateCluster(clusters)
    }

    inner class GalleryScanningCallback: GalleryScanner.ScanningCallback() {
        override fun onProcessingStart(unprocessedMediaCount: Int) {
        }

        override fun onProcessingProgress(
            processedGalleryEntry: ProcessedGalleryEntry,
            progress: Float
        ) {

        }

        override fun onProcessingFinished() {

        }

        override fun onProcessingError(galleryEntry: GalleryEntry, progress: Float) {

        }
    }
}