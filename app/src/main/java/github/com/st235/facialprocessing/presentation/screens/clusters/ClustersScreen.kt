package github.com.st235.facialprocessing.presentation.screens.clusters

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import github.com.st235.facialprocessing.R
import github.com.st235.facialprocessing.presentation.screens.Screen

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ClustersScreen(
    viewModel: ClustersViewModel,
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(true) {
        viewModel.loadState()
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = { Text(stringResource(R.string.clusters_screen_title)) }
            )
        }
    ) { paddings ->
        val faceClusters = state.clusters

        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier = modifier.padding(paddings)
        ) {
            items(faceClusters) { faceCluster ->
                Image(
                    bitmap = faceCluster.sampleFace.asImageBitmap(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1.0f)
                        .padding(8.dp)
                        .clip(CircleShape)
                        .focusable()
                        .clickable { navController.navigate(Screen.Search.creteForCluster(faceCluster.id)) }
                )
            }
        }
    }
}
