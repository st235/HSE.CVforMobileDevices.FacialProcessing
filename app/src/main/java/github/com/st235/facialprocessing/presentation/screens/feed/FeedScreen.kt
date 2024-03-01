package github.com.st235.facialprocessing.presentation.screens.feed

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import github.com.st235.facialprocessing.R
import github.com.st235.facialprocessing.interactors.models.FaceSearchAttribute
import github.com.st235.facialprocessing.interactors.models.MediaEntry
import github.com.st235.facialprocessing.presentation.screens.Screen
import github.com.st235.facialprocessing.presentation.widgets.GridButton
import github.com.st235.facialprocessing.presentation.widgets.SearchAttributesLayout

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun FeedScreen(
    viewModel: FeedViewModel,
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val readExternalStoragePermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                viewModel.startScanning()
            }
        }

    LaunchedEffect(true) {
        viewModel.refreshProcessedData()
    }

    val isProcessing = state.isProcessingImages
    val isClustering = state.isClusteringImages

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
            val canShowScanButton = !isProcessing && !isClustering

            if (canShowScanButton) {
                ExtendedFloatingActionButton(
                    icon = {
                        Icon(
                            painterResource(R.drawable.ic_person_search_24),
                            contentDescription = null
                        )
                    },
                    text = { Text(stringResource(R.string.clustering_feed_screen_scan_button)) },
                    onClick = {
                        if (isReadMediaImagesPermissionGranted(context)) {
                            viewModel.startScanning()
                        } else {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                readExternalStoragePermissionLauncher.launch(READ_MEDIA_IMAGES)
                            } else {
                                readExternalStoragePermissionLauncher.launch(READ_EXTERNAL_STORAGE)
                            }
                        }
                    },
                )
            }
        }
    ) { paddings ->
        if (isProcessing) {
            val progress = state.processingProgress

            SpecialMessageView(
                icon = R.drawable.ic_ar_on_you_24,
                headline = stringResource(R.string.clustering_feed_screen_image_processing_title),
                description = stringResource(R.string.clustering_feed_screen_image_processing_description),
                progress = progress
            )
        } else if (isClustering) {
            SpecialMessageView(
                icon = R.drawable.ic_groups_2_24,
                headline = stringResource(R.string.clustering_feed_screen_clustering_title),
                description = stringResource(R.string.clustering_feed_screen_clustering_description),
                progress = -1f
            )
        } else {
            val processedPhotos = state.imagesWithFaces
            val searchAttributes = state.searchAttributes
            FeedLayout(
                processedPhotos = processedPhotos,
                searchAttributes = searchAttributes,
                onPhotoClick = { navController.navigate(Screen.Details.create(it.id)) },
                onSeeMoreClick = { navController.navigate(Screen.Search.create()) },
                onSearchAttributeClick = {
                    navController.navigate(
                        Screen.Search.createForAttribute(
                            it.id
                        )
                    )
                },
                modifier = Modifier.padding(paddings),
            )
        }
    }
}

@Composable
private fun SpecialMessageView(
    @DrawableRes icon: Int,
    headline: String,
    description: String,
    modifier: Modifier = Modifier,
    headlineColor: Color = MaterialTheme.colorScheme.onSurface,
    descriptionColor: Color = MaterialTheme.colorScheme.onSurface,
    progress: Float? = null,
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Icon(
            painterResource(icon),
            contentDescription = null,
            tint = headlineColor,
            modifier = Modifier
                .width(96.dp)
                .height(96.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = headline,
            color = headlineColor,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            fontSize = 26.sp
        )
        Spacer(modifier = Modifier.height(12.dp))

        if (progress != null) {
            if (progress >= 0f) {
                LinearProgressIndicator(
                    progress = progress,
                    modifier = Modifier.fillMaxWidth(),
                )
            } else {
                CircularProgressIndicator(
                    modifier = Modifier.width(32.dp),
                    color = descriptionColor,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
        }

        Text(
            text = description,
            color = descriptionColor,
            textAlign = TextAlign.Center,
            fontSize = 16.sp
        )
    }
}

@Composable
private fun FeedLayout(
    processedPhotos: List<MediaEntry>,
    searchAttributes: Set<FaceSearchAttribute.Type>,
    onPhotoClick: (MediaEntry) -> Unit,
    onSeeMoreClick: () -> Unit,
    onSearchAttributeClick: (FaceSearchAttribute.Type) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        PhotoCard(
            photos = processedPhotos,
            onPhotoClick = onPhotoClick,
            onSeeMoreClick = onSeeMoreClick,
        )
        SearchAttributesGroup(
            searchAttributes = searchAttributes,
            onSearchAttributeClick = onSearchAttributeClick,
            modifier = Modifier.padding(8.dp),
        )
    }
}

@Composable
private fun PhotoCard(
    photos: List<MediaEntry>,
    modifier: Modifier = Modifier,
    onPhotoClick: (MediaEntry) -> Unit = {},
    onSeeMoreClick: () -> Unit = {},
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
                onPhotoClick = onPhotoClick,
                onSeeMoreClick = onSeeMoreClick,
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
    onPhotoClick: (MediaEntry) -> Unit = {},
    onSeeMoreClick: () -> Unit = {},
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = modifier,
    ) {
        items(photos) { photo ->
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(photo.uri)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.0f)
                    .focusable()
                    .clickable { onPhotoClick(photo) }
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
                    .focusable()
                    .clickable { onSeeMoreClick() }
            )
        }
    }
}

@Composable
private fun SearchAttributesGroup(
    searchAttributes: Set<FaceSearchAttribute.Type>,
    modifier: Modifier = Modifier,
    onSearchAttributeClick: (FaceSearchAttribute.Type) -> Unit,
) {
    Column(modifier = modifier) {
        FeedHeader(textRes = R.string.clustering_feed_attributes_section_title)
        SearchAttributesLayout(
            searchAttributes = searchAttributes,
            onSearchAttributeClick = onSearchAttributeClick,
        )
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

private fun isReadMediaImagesPermissionGranted(context: Context): Boolean {
    val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        READ_MEDIA_IMAGES
    } else {
        READ_EXTERNAL_STORAGE
    }

    return ContextCompat.checkSelfPermission(context, permission) ==
            PackageManager.PERMISSION_GRANTED
}
