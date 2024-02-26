package github.com.st235.facialprocessing.domain

import androidx.annotation.AnyThread
import androidx.annotation.WorkerThread
import github.com.st235.facialprocessing.domain.faces.FaceProcessor
import github.com.st235.facialprocessing.domain.model.GalleryEntry
import github.com.st235.facialprocessing.domain.model.ProcessedGalleryEntry
import github.com.st235.facialprocessing.utils.LocalUriLoader
import github.com.st235.facialprocessing.utils.MediaRetriever
import github.com.st235.facialprocessing.utils.tflite.InterpreterFactory
import java.util.concurrent.Executors
import java.util.concurrent.Future

class GalleryProcessor(
    private val mediaRetriever: MediaRetriever,
    private val localUriLoader: LocalUriLoader,
    private val interpreterFactory: InterpreterFactory,
    private val alreadyProcessedImages: Collection<GalleryEntry>,
) {

    interface OnImagesProcessingListener {
        fun onImageProcessed(entry: ProcessedGalleryEntry, progress: Float)
        fun onProcessingError(entry: GalleryEntry, progress: Float)
        fun onComplete()
    }

    private val executor = Executors.newSingleThreadExecutor()

    @Volatile
    private var asyncJob: Future<*>? = null

    @Volatile
    private var onImagesProcessingListener: OnImagesProcessingListener? = null

    @AnyThread
    @Synchronized
    fun start() {
        if (asyncJob != null) {
            throw IllegalStateException("The job has already started.")
        }

        asyncJob = executor.submit { onStartProcessing() }
    }

    @WorkerThread
    private fun onStartProcessing() {
        val entriesToProcess = getGalleryEntriesToProcess()
        val targetProgress = entriesToProcess.size

        if (targetProgress == 0) {
            notifyComplete()
            return
        }

        val faceProcessor = FaceProcessor(interpreterFactory)
        for ((index, galleryEntry) in entriesToProcess.withIndex()) {
            val progress = (index + 1).toFloat() / targetProgress
            val processedGalleryEntry = faceProcessor.processImage(galleryEntry)

            if (processedGalleryEntry == null) {
                notifyError(galleryEntry, progress)
            } else {
                notifyProgress(processedGalleryEntry, progress)
            }
        }

        notifyComplete()
    }

    @WorkerThread
    private fun FaceProcessor.processImage(galleryEntry: GalleryEntry): ProcessedGalleryEntry? {
        val bitmap = localUriLoader.load(galleryEntry.contentUri)
        if (bitmap == null) {
            return null
        }

        val faceDescriptors = detect(bitmap)
        return ProcessedGalleryEntry(
            id = galleryEntry.id,
            contentUri = galleryEntry.contentUri,
            descriptors = faceDescriptors
        )
    }

    @AnyThread
    @Synchronized
    fun cancel() {
        asyncJob?.cancel(true)
        asyncJob = null
        onImagesProcessingListener = null
    }

    @WorkerThread
    private fun getGalleryEntriesToProcess(): List<GalleryEntry> {
        val processesIdsLookup = alreadyProcessedImages.map { it.id }
        val images = mediaRetriever.queryImages().map { GalleryEntry(it.id, it.uri) }
        return images.filter { !processesIdsLookup.contains(it.id) }
    }

    @Synchronized
    private fun notifyProgress(
        processedGalleryEntry: ProcessedGalleryEntry,
        progress: Float,
    ) {
        onImagesProcessingListener?.onImageProcessed(processedGalleryEntry, progress)
    }

    @Synchronized
    private fun notifyError(
        galleryEntry: GalleryEntry,
        progress: Float,
    ) {
        onImagesProcessingListener?.onProcessingError(galleryEntry, progress)
    }

    @Synchronized
    private fun notifyComplete() {
        onImagesProcessingListener?.onComplete()
    }
}