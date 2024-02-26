package github.com.st235.facialprocessing.domain

import android.net.Uri
import androidx.annotation.WorkerThread
import github.com.st235.facialprocessing.utils.MediaRetriever

class GalleryScanner(
    private val mediaRetriever: MediaRetriever,
) {
    interface OnImagesProcessingListener {
        fun onNewImageProcessed()
    }

    data class GalleryEntry(
        val id: Long,
        val contentUri: Uri,
    )

    @WorkerThread
    fun process(alreadyProcessesImages: Collection<GalleryEntry>) {
        val processesIdsLookup = alreadyProcessesImages.map { it.id }
        val images = mediaRetriever.queryImages().map { GalleryEntry(it.id, it.uri) }
        val imagesToProcess = images.filter { !processesIdsLookup.contains(it.id) }


    }
}
