package github.com.st235.facialprocessing.presentation.screens.feed

import android.graphics.Bitmap
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import github.com.st235.facialprocessing.R
import github.com.st235.facialprocessing.domain.model.FaceDescriptor
import github.com.st235.facialprocessing.interactors.models.MediaEntry
import github.com.st235.facialprocessing.presentation.widgets.FaceOverlayPainter
import github.com.st235.facialprocessing.presentation.widgets.GridButton
import github.com.st235.facialprocessing.utils.sample

private fun FaceDescriptor.Region.asFace(originalImage: Bitmap): FaceOverlayPainter.Face {
    return FaceOverlayPainter.Face(
        left = originalImage.width * left,
        top = originalImage.height * top,
        right = originalImage.width * (left + width),
        bottom = originalImage.height * (top + height),
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun FeedScreen(
    viewModel: FeedViewModel,
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
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = { Text(stringResource(R.string.clustering_feed_screen_title)) }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                icon = { Icon(painterResource(R.drawable.ic_person_search_24), contentDescription = null) },
                text = { Text(stringResource(R.string.clustering_feed_screen_scan_button)) },
                onClick = {},
            )
        }
    ) { paddings ->
        Column(modifier = Modifier.padding(paddings)) {
            ProcessedPhotosCard(
                photos = state.imagesWithFaces,
            )
        }
    }
}

@Composable
private fun ProcessedPhotosCard(
    photos: List<MediaEntry>,
    modifier: Modifier = Modifier,
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.padding(12.dp),
    ) {
        Column {
            FeedHeader(
                textRes = R.string.clustering_feed_all_photos_section_title,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
            ProcessedPhotos(
                photos = photos,
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(32.dp))
            )
        }
    }
}

@Composable
private fun ProcessedPhotos(
    photos: List<MediaEntry>,
    modifier: Modifier = Modifier,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = modifier,
    ) {
        items(photos.sample(5)) { photo ->
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(photo.uri)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.0f)
            )
        }

        item {
            GridButton(
                iconRes = R.drawable.ic_hallway_24,
                text = stringResource(R.string.clustering_feed_grid_see_more),
                textColor = MaterialTheme.colorScheme.onPrimaryContainer,
                backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.0f)
            )
        }
    }
}

@Composable
private fun FeedHeader(
    @StringRes textRes: Int,
    modifier: Modifier = Modifier,
    textColor: Color = MaterialTheme.colorScheme.onBackground,
    descriptionColor: Color = MaterialTheme.colorScheme.onSecondaryContainer,
) {
    Column(modifier = modifier) {
        Text(
            stringResource(textRes),
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
            color = textColor,
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            stringResource(textRes),
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            color = descriptionColor,
        )
    }
}