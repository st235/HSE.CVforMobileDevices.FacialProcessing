package github.com.st235.facialprocessing.presentation.screens.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import github.com.st235.facialprocessing.R
import github.com.st235.facialprocessing.interactors.models.FaceCluster
import github.com.st235.facialprocessing.interactors.models.FaceSearchAttribute
import github.com.st235.facialprocessing.presentation.screens.Screen
import github.com.st235.facialprocessing.presentation.widgets.FaceClusterView
import github.com.st235.facialprocessing.presentation.widgets.SearchAttributeView
import github.com.st235.facialprocessing.utils.iconRes
import github.com.st235.facialprocessing.utils.textRes

@Composable
@OptIn(ExperimentalMaterial3Api::class, ExperimentalGlideComposeApi::class)
fun SearchScreen(
    personId: Int?,
    searchAttributeIds: List<Int>,
    viewModel: SearchViewModel,
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(true) {
        viewModel.loadState(personId, searchAttributeIds)
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    val faceCluster = state.faceCluster
                    val searchAttributes = state.searchAttributeTypes

                    TitleView(
                        faceCluster = faceCluster,
                        searchAttributes = searchAttributes,
                    )
                },
                navigationIcon = {
                    if (navController.previousBackStackEntry != null) {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(
                                painter = painterResource(R.drawable.ic_arrow_back_24),
                                tint = MaterialTheme.colorScheme.primary,
                                contentDescription = null
                            )
                        }
                    }
                },
                modifier = Modifier.shadow(elevation = 8.dp)
            )
        }
    ) { paddings ->
        val photos = state.images

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = modifier
                .fillMaxSize()
                .padding(paddings),
        ) {
            items(photos) { photo ->
                GlideImage(
                    model = photo.uri,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(32.dp))
                        .focusable()
                        .clickable {
                            navController.navigate(
                                Screen.Details.create(
                                    photo.id,
                                    personId
                                )
                            )
                        }
                )
            }
        }
    }
}

@Composable
private fun TitleView(
    faceCluster: FaceCluster?,
    searchAttributes: List<FaceSearchAttribute.Type>,
    modifier: Modifier = Modifier,
) {
    val detailedSearch = faceCluster != null || searchAttributes.isNotEmpty()

    if (!detailedSearch) {
        Text(
            stringResource(R.string.search_screen_generic_title),
            fontWeight = FontWeight.Medium,
            modifier = modifier,
        )
    } else {
        Row(
           verticalAlignment = Alignment.CenterVertically,
            modifier = modifier,
        ) {
            Text(
                stringResource(R.string.search_screen_specific_title),
                fontWeight = FontWeight.Medium,
                modifier = modifier,
            )

            if (faceCluster != null) {
                Spacer(modifier = Modifier.width(8.dp))
                FaceClusterView(
                    face = faceCluster.sampleFace,
                    text = stringResource(R.string.search_screen_cluster_placeholder),
                    textSize = 18.sp,
                    iconSize = 18.dp,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    backgroundColor = MaterialTheme.colorScheme.surface,
                )
            }

            for (searchAttribute in searchAttributes) {
                Spacer(modifier = Modifier.width(8.dp))
                SearchAttributeView(
                    iconRes = searchAttribute.iconRes,
                    text = stringResource(searchAttribute.textRes),
                    textSize = 18.sp,
                    iconSize = 18.dp,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    backgroundColor = MaterialTheme.colorScheme.surface,
                )
            }
        }
    }
}
