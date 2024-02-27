package github.com.st235.facialprocessing.domain

import androidx.annotation.WorkerThread
import github.com.st235.facialprocessing.data.FacesRepository
import github.com.st235.facialprocessing.data.db.FaceEntity
import github.com.st235.facialprocessing.data.db.MediaFileEntity
import github.com.st235.facialprocessing.domain.model.FaceDescriptor
import github.com.st235.facialprocessing.domain.model.FaceDescriptor.Gender.Companion.toInt
import github.com.st235.facialprocessing.domain.model.GalleryEntry
import github.com.st235.facialprocessing.domain.model.ProcessedGalleryEntry
import github.com.st235.facialprocessing.utils.LocalUriLoader
import github.com.st235.facialprocessing.utils.MediaRetriever
import github.com.st235.facialprocessing.utils.Observable
import github.com.st235.facialprocessing.utils.tflite.InterpreterFactory
import java.util.concurrent.Executors
import java.util.concurrent.Future

class GalleryScanner(
    private val facesRepository: FacesRepository,
    private val mediaRetriever: MediaRetriever,
    private val localUriLoader: LocalUriLoader,
    private val interpreterFactory: InterpreterFactory,
): Observable<GalleryScanner.ScanningCallback>() {

    abstract class ScanningCallback {
        open fun onProcessingStart(unprocessedMediaCount: Int) {}
        abstract fun onProcessingProgress(processedGalleryEntry: ProcessedGalleryEntry, progress: Float)
        open fun onProcessingFinished() {}
        open fun onProcessingError(galleryEntry: GalleryEntry, progress: Float) {}
    }

    private val executor = Executors.newSingleThreadExecutor()

    @Volatile
    private var scanningFuture: Future<*>? = null

    fun start() {
        synchronized(this) {
            if (scanningFuture != null) {
                throw IllegalStateException("Cannot start, scanning has already started.")
            }

            scanningFuture = executor.submit(ScanningRequest())
        }
    }

    fun cancel() {
        synchronized(this) {
            if (scanningFuture == null) {
                throw IllegalStateException("Cannot cancel, scanning is not running.")
            }

            scanningFuture?.cancel(true)
            scanningFuture = null
        }
    }

    private fun onProcessingStart(unprocessedMediaCount: Int) {
        notifyCallbacks { it.onProcessingStart(unprocessedMediaCount) }
    }

    private fun onProcessingProgress(processedGalleryEntry: ProcessedGalleryEntry, progress: Float) {
        notifyCallbacks { it.onProcessingProgress(processedGalleryEntry, progress) }
    }

    private fun onProcessingError(galleryEntry: GalleryEntry, progress: Float) {
        notifyCallbacks { it.onProcessingError(galleryEntry, progress) }
    }

    private fun onProcessingFinished() {
        notifyCallbacks { it.onProcessingFinished() }
    }

    @WorkerThread
    private inner class ScanningRequest: Runnable {

        override fun run() {
            val mediaFilesProcessor = MediaFilesProcessor.create(localUriLoader, interpreterFactory)

            val unprocessedGalleryEntries = getUnprocessedGalleryEntries()

            if (unprocessedGalleryEntries.isEmpty()) {
                onProcessingFinished()
                return
            }

            val unprocessedEntriesSize = unprocessedGalleryEntries.size
            onProcessingStart(unprocessedEntriesSize)

            for ((index, galleryEntry) in unprocessedGalleryEntries.withIndex()) {
                val progress = (index + 1).toFloat() / unprocessedEntriesSize

                val processingResult = mediaFilesProcessor.process(galleryEntry)

                if (processingResult.isFailure) {
                    onProcessingError(galleryEntry, progress)
                } else {
                    val processedGalleryEntry = processingResult.getOrThrow()
                    val descriptors = processedGalleryEntry.descriptors

                    facesRepository.insert(
                        MediaFileEntity(
                            mediaId = processedGalleryEntry.id,
                            mediaUrl = processedGalleryEntry.contentUri.toString(),
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
                                hasBeard = attributes.contains(FaceDescriptor.Attribute.BEARD),
                                hasMustache = attributes.contains(FaceDescriptor.Attribute.MUSTACHE),
                                hasGlasses = attributes.contains(FaceDescriptor.Attribute.GLASSES),
                                isSmiling = attributes.contains(FaceDescriptor.Attribute.SMILE),
                                embeddings = it.embeddings,
                            )
                        }
                    )

                    onProcessingProgress(processedGalleryEntry, progress)
                }
            }
        }

        @WorkerThread
        private fun getUnprocessedGalleryEntries(): List<GalleryEntry> {
            val alreadyProcessedImages = facesRepository.getProcessedMediaFiles()
            val processesIdsLookup = alreadyProcessedImages.map { it.mediaId }
            val images = mediaRetriever.queryImages().map { GalleryEntry(it.id, it.uri) }
            return images.filter { !processesIdsLookup.contains(it.id) }
        }
    }
}