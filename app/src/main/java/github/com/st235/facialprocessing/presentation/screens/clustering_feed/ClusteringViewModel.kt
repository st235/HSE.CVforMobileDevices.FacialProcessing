package github.com.st235.facialprocessing.presentation.screens.clustering_feed

import android.util.Log
import github.com.st235.facialprocessing.presentation.base.BaseViewModel
import github.com.st235.facialprocessing.utils.MediaRetriever
import kotlinx.coroutines.launch

class ClusteringViewModel(
    private val mediaRetriever: MediaRetriever
): BaseViewModel() {

    fun loadAllPhotos() {
        backgroundScope.launch {
            val images = mediaRetriever.queryImages()

            for (image in images) {
                Log.d("HelloWorld", "Gallery object: $image")
            }
        }
    }

}