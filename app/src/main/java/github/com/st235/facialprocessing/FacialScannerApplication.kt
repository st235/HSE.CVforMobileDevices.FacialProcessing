package github.com.st235.facialprocessing

import android.app.Application
import android.util.Log
import github.com.st235.facialprocessing.di.appModules
import github.com.st235.facialprocessing.domain.GalleryScanner
import github.com.st235.facialprocessing.domain.MediaFilesProcessor
import github.com.st235.facialprocessing.domain.model.GalleryEntry
import github.com.st235.facialprocessing.domain.model.ProcessedGalleryEntry
import github.com.st235.facialprocessing.utils.LocalUriLoader
import github.com.st235.facialprocessing.utils.MediaRetriever
import github.com.st235.facialprocessing.utils.tflite.InterpreterFactory
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class FacialScannerApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@FacialScannerApplication)
            modules(appModules)

            val galleryScanner = get<GalleryScanner>()

            galleryScanner.addCallback(object : GalleryScanner.ScanningCallback() {
                override fun onProcessingStart(unprocessedMediaCount: Int) {
                    Log.d("FacialScannerApp", "onProcessingStart: $unprocessedMediaCount")
                }

                override fun onProcessingProgress(
                    processedGalleryEntry: ProcessedGalleryEntry,
                    progress: Float
                ) {
                    Log.d("FacialScannerApp", "onProcessingProgress: $progress, $processedGalleryEntry")
                }

                override fun onProcessingFinished() {
                    Log.d("FacialScannerApp", "onProcessingFinished")
                }

                override fun onProcessingError(galleryEntry: GalleryEntry, progress: Float) {
                    Log.d("FacialScannerApp", "onProcessingError: $progress, $galleryEntry")
                }
            })

            galleryScanner.start()
        }
    }
}