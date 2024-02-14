package github.com.st235.facialprocessing.di

import github.com.st235.facialprocessing.presentation.screens.clustering_feed.ClusteringViewModel
import github.com.st235.facialprocessing.utils.GalleryScanner
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

private val viewModelsModule = module {

    viewModel { ClusteringViewModel(get()) }

}

private val utilsModule = module {

    single { androidContext().contentResolver }

    factory { GalleryScanner(get()) }

}

val appModules = viewModelsModule + utilsModule
