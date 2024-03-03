package github.com.st235.facialprocessing.presentation.screens.feed

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
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
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import github.com.st235.facialprocessing.R
import github.com.st235.facialprocessing.interactors.models.FaceCluster
import github.com.st235.facialprocessing.interactors.models.FaceSearchAttribute
import github.com.st235.facialprocessing.interactors.models.MediaEntry
import github.com.st235.facialprocessing.presentation.screens.Screen
import github.com.st235.facialprocessing.presentation.widgets.GridButton
import github.com.st235.facialprocessing.presentation.widgets.GridLayout
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

    val currentStatus = state.status

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = { Text(stringResource(R.string.clustering_feed_screen_title), fontWeight = FontWeight.Medium) },
                modifier = Modifier.shadow(elevation = 8.dp)
            )
        },
        floatingActionButton = {
            if (currentStatus.canShowScanButton) {
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
        when (currentStatus) {
            FeedUiState.Status.PREPARING_TO_PROCESSING -> {
                SpecialMessageView(
                    icon = R.drawable.ic_image_search_24,
                    headline = stringResource(R.string.clustering_feed_screen_preparing_to_processing_title),
                    description = stringResource(R.string.clustering_feed_screen_preparing_to_processing_description),
                    progress = -1f,
                    modifier = Modifier
                        .padding(paddings)
                        .fillMaxSize()
                        .padding(64.dp),
                )
            }

            FeedUiState.Status.PROCESSING_IMAGES -> {
                val progress = state.processingProgress
                val unprocessedPhotosCount = state.photosToProcessCount

                SpecialMessageView(
                    icon = R.drawable.ic_ar_on_you_24,
                    headline = stringResource(R.string.clustering_feed_screen_image_processing_title),
                    description = stringResource(R.string.clustering_feed_screen_image_processing_description, unprocessedPhotosCount),
                    progress = progress,
                    modifier = Modifier
                        .padding(paddings)
                        .fillMaxSize()
                        .padding(64.dp),
                )
            }

            FeedUiState.Status.CLUSTERING -> {
                SpecialMessageView(
                    icon = R.drawable.ic_groups_2_24,
                    headline = stringResource(R.string.clustering_feed_screen_clustering_title),
                    description = stringResource(R.string.clustering_feed_screen_clustering_description),
                    progress = -1f,
                    modifier = Modifier
                        .padding(paddings)
                        .fillMaxSize()
                        .padding(64.dp),
                )
            }

            FeedUiState.Status.LOADING_DATA -> {
                SpecialMessageView(
                    icon = R.drawable.ic_hourglass_top_24,
                    headline = stringResource(R.string.clustering_feed_loading_title),
                    description = stringResource(R.string.clustering_feed_loading_description),
                    progress = -1f,
                    modifier = Modifier
                        .padding(paddings)
                        .fillMaxSize()
                        .padding(64.dp),
                )
            }

            FeedUiState.Status.READY -> {
                val processedPhotos = state.imagesWithFaces
                val searchAttributes = state.searchAttributes
                val faceClusters = state.faceClusters

                FeedLayout(
                    processedPhotos = processedPhotos,
                    searchAttributes = searchAttributes,
                    faceClusters = faceClusters,
                    onPhotoClick = { navController.navigate(Screen.Details.create(it.id)) },
                    onSeeMorePhotosClick = { navController.navigate(Screen.Search.create()) },
                    onClusterClick = { navController.navigate(Screen.Search.creteForCluster(it.clusterId)) },
                    onSeeMoreClustersClick = { navController.navigate(Screen.Clusters.route) },
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
}

@Composable
private fun SpecialMessageView(
    @DrawableRes icon: Int,
    headline: String,
    description: String,
    modifier: Modifier = Modifier,
    headlineColor: Color = MaterialTheme.colorScheme.onBackground,
    descriptionColor: Color = MaterialTheme.colorScheme.onBackground,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    onBackgroundColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    progress: Float? = null,
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Icon(
            painterResource(icon),
            contentDescription = null,
            tint = onBackgroundColor,
            modifier = Modifier
                .width(96.dp)
                .height(96.dp)
                .background(backgroundColor, CircleShape)
                .padding(24.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (progress != null && progress < 0f) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .width(16.dp)
                        .height(16.dp),
                    color = descriptionColor,
                    trackColor = backgroundColor,
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = headline,
                color = headlineColor,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                fontSize = 26.sp
            )
        }
        Spacer(modifier = Modifier.height(12.dp))

        if (progress != null && progress >= 0f) {
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier.fillMaxWidth(),
                trackColor = backgroundColor,
            )
            Spacer(modifier = Modifier.height(8.dp))
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
    faceClusters: List<FaceCluster>,
    onPhotoClick: (MediaEntry) -> Unit,
    onSeeMorePhotosClick: () -> Unit,
    onClusterClick: (FaceCluster) -> Unit,
    onSeeMoreClustersClick: () -> Unit,
    onSearchAttributeClick: (FaceSearchAttribute.Type) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        PhotoCard(
            photos = processedPhotos,
            onPhotoClick = onPhotoClick,
            onSeeMorePhotosClick = onSeeMorePhotosClick,
        )
        ClustersGroup(
            faceClusters = faceClusters,
            onClusterClick = onClusterClick,
            onSeeMoreClustersClick = onSeeMoreClustersClick,
            modifier = Modifier
                .padding(vertical = 8.dp, horizontal = 16.dp)
        )
        SearchAttributesGroup(
            searchAttributes = searchAttributes,
            onSearchAttributeClick = onSearchAttributeClick,
            modifier = Modifier
                .padding(vertical = 8.dp, horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(72.dp))
    }
}

@Composable
private fun PhotoCard(
    photos: List<MediaEntry>,
    modifier: Modifier = Modifier,
    onPhotoClick: (MediaEntry) -> Unit = {},
    onSeeMorePhotosClick: () -> Unit = {},
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        shape = RoundedCornerShape(18.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
    ) {
        Column {
            FeedHeader(
                text = stringResource(R.string.clustering_feed_all_photos_section_title),
                description = stringResource(R.string.clustering_feed_all_photos_section_description),
                textColor = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp)
            )
            ProcessedPhotos(
                photos = photos,
                onPhotoClick = onPhotoClick,
                onSeeMorePhotosClick = onSeeMorePhotosClick,
                modifier = Modifier
                    .padding(horizontal = 8.dp, vertical = 8.dp)
            )
        }
    }
}

@Composable
@OptIn(ExperimentalGlideComposeApi::class)
private fun ProcessedPhotos(
    photos: List<MediaEntry>,
    modifier: Modifier = Modifier,
    onPhotoClick: (MediaEntry) -> Unit = {},
    onSeeMorePhotosClick: () -> Unit = {},
) {
    GridLayout(
        columns = 3,
        modifier = modifier,
    ) {
        for (photo in photos) {
            GlideImage(
                model = photo.uri,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.0f)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .focusable()
                    .clickable { onPhotoClick(photo) }
            )
        }

        GridButton(
            iconRes = R.drawable.ic_hallway_24,
            text = stringResource(R.string.clustering_feed_grid_see_more),
            color = MaterialTheme.colorScheme.onSurface,
            backgroundColor = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.0f)
                .padding(8.dp)
                .clip(RoundedCornerShape(32.dp))
                .focusable()
                .clickable { onSeeMorePhotosClick() }
        )
    }
}

@Composable
private fun ClustersGroup(
    faceClusters: List<FaceCluster>,
    modifier: Modifier = Modifier,
    onClusterClick: (FaceCluster) -> Unit = {},
    onSeeMoreClustersClick: () -> Unit = {},
) {
    Column(modifier = modifier) {
        FeedHeader(
            text = stringResource(R.string.clustering_feed_clusters_section_title),
            description = stringResource(R.string.clustering_feed_screen_clustering_description),
        )
        GridLayout(
            columns = 4,
            modifier = modifier
                .fillMaxWidth(),
        ) {
            for (faceCluster in faceClusters) {
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
                        .clickable { onClusterClick(faceCluster) }
                )
            }

            GridButton(
                iconRes = R.drawable.ic_face_24,
                text = stringResource(R.string.clustering_feed_grid_see_more),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                fontSize = 12.sp,
                iconSpacing = 2.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.0f)
                    .padding(8.dp)
                    .clip(CircleShape)
                    .focusable()
                    .clickable { onSeeMoreClustersClick() }
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
        FeedHeader(
            text = stringResource(R.string.clustering_feed_attributes_section_title),
            description = stringResource(R.string.clustering_feed_attributes_section_description),
        )
        Spacer(modifier = Modifier.height(12.dp))
        SearchAttributesLayout(
            searchAttributes = searchAttributes,
            onSearchAttributeClick = onSearchAttributeClick,
        )
    }
}

@Composable
private fun FeedHeader(
    text: String,
    description: String,
    modifier: Modifier = Modifier,
    textColor: Color = MaterialTheme.colorScheme.onBackground,
) {
    Column(modifier = modifier) {
        Text(
            text,
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
            color = textColor,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            description,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            color = textColor,
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
