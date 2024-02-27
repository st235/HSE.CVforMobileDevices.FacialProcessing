package github.com.st235.facialprocessing.di

import androidx.room.Room
import github.com.st235.facialprocessing.data.FacesRepository
import github.com.st235.facialprocessing.data.db.FaceScannerDatabase
import github.com.st235.facialprocessing.domain.GalleryScanner
import github.com.st235.facialprocessing.domain.MediaFilesProcessor
import github.com.st235.facialprocessing.utils.tflite.InterpreterFactory
import github.com.st235.facialprocessing.presentation.screens.clustering_feed.ClusteringViewModel
import github.com.st235.facialprocessing.utils.LocalUriLoader
import github.com.st235.facialprocessing.utils.MediaRetriever
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

private val dataModule = module {

    single {
        Room.databaseBuilder(androidContext(), FaceScannerDatabase::class.java, "face_scanner")
            .build()
    }

    single { FacesRepository(get()) }

}

private val viewModelsModule = module {

    viewModel { ClusteringViewModel(get()) }

}

private val domainModule = module {

    single { GalleryScanner(get(), get(), get(), get()) }

}

private val utilsModule = module {

    single { androidContext().contentResolver }

    factory { LocalUriLoader(get()) }

    factory { MediaRetriever(get()) }

    factory { InterpreterFactory(androidContext()) }

}

val appModules = dataModule + viewModelsModule + domainModule + utilsModule
