package github.com.st235.facialprocessing.presentation.screens.clustering_feed

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

@Composable
fun ClusteringFeed(
    viewModel: ClusteringViewModel,
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    LaunchedEffect("view_model") {
        viewModel.loadAllPhotos()
    }

    Text("Hello world!")
}
