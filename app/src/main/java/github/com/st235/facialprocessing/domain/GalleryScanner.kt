package github.com.st235.facialprocessing.domain

import androidx.annotation.WorkerThread
import github.com.st235.facialprocessing.data.FacesRepository
import github.com.st235.facialprocessing.data.db.FaceEntity
import github.com.st235.facialprocessing.data.db.FaceWithMediaFileEntity
import github.com.st235.facialprocessing.data.db.MediaFileEntity
import github.com.st235.facialprocessing.domain.clustering.Clusterer
import github.com.st235.facialprocessing.domain.model.FaceDescriptor
import github.com.st235.facialprocessing.domain.model.FaceDescriptor.Emotion.Companion.toInt
import github.com.st235.facialprocessing.domain.model.FaceDescriptor.Gender.Companion.toInt
import github.com.st235.facialprocessing.domain.model.GalleryEntry
import github.com.st235.facialprocessing.domain.model.ProcessedGalleryEntry
import github.com.st235.facialprocessing.utils.Assertion
import github.com.st235.facialprocessing.utils.LocalUriLoader
import github.com.st235.facialprocessing.utils.MediaRetriever
import github.com.st235.facialprocessing.utils.tflite.InterpreterFactory

class GalleryScanner(
    private val facesRepository: FacesRepository,
    private val mediaRetriever: MediaRetriever,
    private val localUriLoader: LocalUriLoader,
    private val interpreterFactory: InterpreterFactory,
    private val clusterer: Clusterer<FaceWithMediaFileEntity>,
) {

    abstract class ScanningCallback {
        open fun onProcessingStart(unprocessedMediaCount: Int) {}
        abstract fun onProcessingProgress(processedGalleryEntry: ProcessedGalleryEntry, progress: Float)
        open fun onProcessingError(galleryEntry: GalleryEntry, progress: Float) {}
        open fun onProcessingFinished() {}
        open fun onClusteringStarted() {}
        open fun onClusteringFinished() {}
    }

    @WorkerThread
    fun start(callback: ScanningCallback) {
        Assertion.assertOnWorkerThread()
        onStart(callback)
    }

    @WorkerThread
    private fun onStart(callback: ScanningCallback) {
        val mediaFilesProcessor = MediaFilesProcessor.create(localUriLoader, interpreterFactory)

        val unprocessedGalleryEntries = getUnprocessedGalleryEntries()

        if (unprocessedGalleryEntries.isNotEmpty()) {
            val unprocessedEntriesSize = unprocessedGalleryEntries.size
            callback.onProcessingStart(unprocessedEntriesSize)

            for ((index, galleryEntry) in unprocessedGalleryEntries.withIndex()) {
                val progress = (index + 1).toFloat() / unprocessedEntriesSize

                val processingResult = mediaFilesProcessor.process(galleryEntry)

                if (processingResult.isFailure) {
                    callback.onProcessingError(galleryEntry, progress)
                } else {
                    val processedGalleryEntry = processingResult.getOrThrow()
                    val descriptors = processedGalleryEntry.descriptors

                    facesRepository.insert(
                        MediaFileEntity(
                            mediaId = processedGalleryEntry.id,
                            mediaUri = processedGalleryEntry.contentUri.toString(),
                        ),
                        descriptors.map {
                            val region = it.region
                            val attributes = it.attributes

                            FaceEntity(
                                left = region.left,
                                top = region.top,
                                width = region.width,
                                height = region.height,
                                mediaId = processedGalleryEntry.id,
                                age = it.age,
                                gender = it.gender.toInt(),
                                emotion = it.emotion.toInt(),
                                hasBeard = attributes.contains(FaceDescriptor.Attribute.BEARD),
                                hasMustache = attributes.contains(FaceDescriptor.Attribute.MUSTACHE),
                                hasGlasses = attributes.contains(FaceDescriptor.Attribute.GLASSES),
                                isSmiling = attributes.contains(FaceDescriptor.Attribute.SMILE),
                                embeddings = it.embeddings,
                            )
                        }
                    )

                    callback.onProcessingProgress(processedGalleryEntry, progress)
                }
            }
        }

        callback.onProcessingFinished()
        callback.onClusteringStarted()

        val allFaces = facesRepository.getAllFaces()
        val clusters = clusterer.cluster(allFaces)

        val clustersLookup = mutableMapOf<Int, Int>()

        for ((clusterId, cluster) in clusters.withIndex()) {
            for (face in cluster) {
                clustersLookup[face.id] = clusterId
            }
        }

        facesRepository.insertClusters(clustersLookup)

        callback.onClusteringFinished()
    }

    @WorkerThread
    private fun getUnprocessedGalleryEntries(): List<GalleryEntry> {
        val alreadyProcessedImages = facesRepository.getProcessedMediaFiles()
        val processesIdsLookup = alreadyProcessedImages.map { it.mediaId }
        val images = mediaRetriever.queryImages().map { GalleryEntry(it.id, it.uri) }
        return images.filter { !processesIdsLookup.contains(it.id) }
    }
}