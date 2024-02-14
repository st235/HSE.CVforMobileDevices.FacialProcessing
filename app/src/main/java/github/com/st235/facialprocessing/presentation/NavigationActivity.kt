package github.com.st235.facialprocessing.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import github.com.st235.facialprocessing.presentation.base.theme.FacialProcessingTheme
import github.com.st235.facialprocessing.presentation.screens.Screen
import github.com.st235.facialprocessing.presentation.screens.clustering_feed.ClusteringFeed
import github.com.st235.facialprocessing.presentation.screens.clustering_feed.ClusteringViewModel
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FacialProcessingTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavHost(
                        startDestination = Screen.ClusteringFeed.route,
                        navController = rememberNavController()
                    )
                }
            }
        }
    }
}

@Composable
fun AppNavHost(
    startDestination: String,
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        startDestination = startDestination,
        navController = navController,
        modifier = modifier
    ) {
        composable(Screen.ClusteringFeed.route) {
            val viewModel = koinViewModel<ClusteringViewModel>()

            ClusteringFeed(
                viewModel = viewModel,
                navController = navController,
                modifier = modifier
            )
        }
    }
}
