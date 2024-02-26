package github.com.st235.facialprocessing.di

import github.com.st235.facialprocessing.utils.tflite.InterpreterFactory
import github.com.st235.facialprocessing.presentation.screens.clustering_feed.ClusteringViewModel
import github.com.st235.facialprocessing.utils.MediaRetriever
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

private val viewModelsModule = module {

    viewModel { ClusteringViewModel(get()) }

}

private val domainModule = module {

    factory { InterpreterFactory(androidContext()) }

}

private val utilsModule = module {

    single { androidContext().contentResolver }

    factory { MediaRetriever(get()) }

}

val appModules = viewModelsModule + domainModule + utilsModule
