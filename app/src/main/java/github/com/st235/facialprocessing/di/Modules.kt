package github.com.st235.facialprocessing.di

import androidx.room.Room
import github.com.st235.facialprocessing.data.FacesRepository
import github.com.st235.facialprocessing.data.db.FaceScannerDatabase
import github.com.st235.facialprocessing.data.db.FaceWithMediaFileEntity
import github.com.st235.facialprocessing.domain.FaceDistanceMetric
import github.com.st235.facialprocessing.domain.GalleryScanner
import github.com.st235.facialprocessing.domain.clustering.Clusterer
import github.com.st235.facialprocessing.domain.clustering.Distance
import github.com.st235.facialprocessing.interactors.ClustersInteractor
import github.com.st235.facialprocessing.interactors.DetailsInteractor
import github.com.st235.facialprocessing.interactors.FeedInteractor
import github.com.st235.facialprocessing.interactors.SearchInteractor
import github.com.st235.facialprocessing.presentation.screens.clusters.ClustersViewModel
import github.com.st235.facialprocessing.presentation.screens.details.DetailsViewModel
import github.com.st235.facialprocessing.presentation.screens.feed.FeedViewModel
import github.com.st235.facialprocessing.presentation.screens.search.SearchViewModel
import github.com.st235.facialprocessing.utils.LocalUriLoader
import github.com.st235.facialprocessing.utils.MediaRetriever
import github.com.st235.facialprocessing.utils.tflite.InterpreterFactory
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

private val domainModule = module {

    single<Distance<FaceWithMediaFileEntity>> { FaceDistanceMetric() }

    single<Clusterer<FaceWithMediaFileEntity>> { Clusterer.create(get(), Clusterer.Algorithm.HDBSCAN) }

    single { GalleryScanner(get(), get(), get(), get(), get()) }

}

private val interactorsModule = module {

    single { FeedInteractor(get(), get(), get()) }

    single { DetailsInteractor(get(), get()) }

    single { SearchInteractor(get()) }

    single { ClustersInteractor(get(), get()) }

}

private val viewModelsModule = module {

    viewModel { FeedViewModel(get()) }

    viewModel { DetailsViewModel(get()) }

    viewModel { SearchViewModel(get()) }

    viewModel { ClustersViewModel(get()) }

}

private val utilsModule = module {

    single { androidContext().contentResolver }

    factory { LocalUriLoader(get()) }

    factory { MediaRetriever(get()) }

    factory { InterpreterFactory(androidContext()) }

}

val appModules = dataModule + domainModule + interactorsModule + viewModelsModule + utilsModule
