package github.com.st235.facialprocessing.domain

import androidx.annotation.WorkerThread
import github.com.st235.facialprocessing.domain.faces.FaceProcessor
import github.com.st235.facialprocessing.domain.model.GalleryEntry
import github.com.st235.facialprocessing.domain.model.ProcessedGalleryEntry
import github.com.st235.facialprocessing.utils.LocalUriLoader
import github.com.st235.facialprocessing.utils.tflite.InterpreterFactory

class MediaFilesProcessor(
    private val faceProcessor: FaceProcessor,
    private val localUriLoader: LocalUriLoader,
) {

    @WorkerThread
    companion object {
        fun create(
            localUriLoader: LocalUriLoader,
            interpreterFactory: InterpreterFactory,
        ): MediaFilesProcessor {
            return MediaFilesProcessor(FaceProcessor(interpreterFactory), localUriLoader)
        }
    }

    class ProcessingException : RuntimeException()

    @WorkerThread
    fun process(galleryEntry: GalleryEntry): Result<ProcessedGalleryEntry> {
        val bitmap = localUriLoader.load(galleryEntry.contentUri)
        if (bitmap == null) {
            return Result.failure(ProcessingException())
        }

        val faceDescriptors = faceProcessor.detect(bitmap)
        return Result.success(
            ProcessedGalleryEntry(
                id = galleryEntry.id,
                contentUri = galleryEntry.contentUri,
                descriptors = faceDescriptors
            )
        )
    }
}