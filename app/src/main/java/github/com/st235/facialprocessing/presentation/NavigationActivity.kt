package github.com.st235.facialprocessing.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import github.com.st235.facialprocessing.presentation.theme.FacialProcessingTheme
import github.com.st235.facialprocessing.presentation.screens.Screen
import github.com.st235.facialprocessing.presentation.screens.details.DetailsScreen
import github.com.st235.facialprocessing.presentation.screens.details.DetailsViewModel
import github.com.st235.facialprocessing.presentation.screens.feed.FeedScreen
import github.com.st235.facialprocessing.presentation.screens.feed.FeedViewModel
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
                        startDestination = Screen.Feed.route,
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
        composable(Screen.Feed.route) {
            val viewModel = koinViewModel<FeedViewModel>()

            FeedScreen(
                viewModel = viewModel,
                navController = navController,
                modifier = modifier
            )
        }

        composable(
            route = Screen.Details.route,
            arguments = listOf(
                navArgument(Screen.Details.MEDIA_ID) { type = NavType.IntType },
                navArgument(Screen.Details.FACE_ID) { type = NavType.IntType },
            )
        ) { backStackEntry ->
            val mediaId = backStackEntry.arguments?.getInt(Screen.Details.MEDIA_ID) ?: -1
            val faceId = backStackEntry.arguments?.getInt(Screen.Details.FACE_ID) ?: -1

            val viewModel = koinViewModel<DetailsViewModel>()

            DetailsScreen(
                mediaId = mediaId,
                faceId = if (faceId == Screen.Details.FACE_NULL) { null } else {  faceId },
                viewModel = viewModel,
                navController = navController,
                modifier = modifier
            )
        }
    }
}
