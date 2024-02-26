package github.com.st235.facialprocessing

import android.app.Application
import github.com.st235.facialprocessing.di.appModules
import github.com.st235.facialprocessing.domain.GalleryProcessor
import github.com.st235.facialprocessing.domain.model.GalleryEntry
import github.com.st235.facialprocessing.utils.LocalUriLoader
import github.com.st235.facialprocessing.utils.MediaRetriever
import github.com.st235.facialprocessing.utils.tflite.InterpreterFactory
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class FacialScannerApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        val galleryScanner = GalleryProcessor(MediaRetriever(contentResolver), LocalUriLoader(contentResolver), InterpreterFactory(this), emptyList())
        galleryScanner.start()

        startKoin {
            androidContext(this@FacialScannerApplication)
            modules(appModules)
        }
    }
}