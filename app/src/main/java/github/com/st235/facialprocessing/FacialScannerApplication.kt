package github.com.st235.facialprocessing

import android.app.Application
import github.com.st235.facialprocessing.di.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class FacialScannerApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@FacialScannerApplication)
            modules(appModules)
        }
    }
}