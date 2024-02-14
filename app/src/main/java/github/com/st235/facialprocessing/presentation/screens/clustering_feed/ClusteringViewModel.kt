package github.com.st235.facialprocessing.presentation.screens.clustering_feed

import android.util.Log
import github.com.st235.facialprocessing.presentation.base.BaseViewModel
import github.com.st235.facialprocessing.utils.GalleryScanner
import kotlinx.coroutines.launch

class ClusteringViewModel(
    private val galleryScanner: GalleryScanner
): BaseViewModel() {

    fun loadAllPhotos() {
        backgroundScope.launch {
            val images = galleryScanner.queryImages()

            for (image in images) {
                Log.d("HelloWorld", "Gallery object: $image")
            }
        }
    }

}