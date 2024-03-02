package github.com.st235.facialprocessing.presentation.screens.details

import android.graphics.Bitmap
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import github.com.st235.facialprocessing.R
import github.com.st235.facialprocessing.domain.model.FaceDescriptor
import github.com.st235.facialprocessing.presentation.widgets.FaceOverlay
import github.com.st235.facialprocessing.presentation.widgets.FaceView
import github.com.st235.facialprocessing.presentation.widgets.SearchAttributesLayout

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun DetailsScreen(
    mediaId: Int,
    faceId: Int?,
    viewModel: DetailsViewModel,
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(true) {
        viewModel.loadState(mediaId)
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = { Text(stringResource(R.string.details_screen_title), fontWeight = FontWeight.Medium) },
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
        val image = state.bitmap

        if (image != null) {
            val faceDescriptors = state.faceDescriptors
            val searchAttributes = state.searchAttributes

            Column(
                modifier = Modifier.padding(paddings)
            ) {
                FaceView(
                    image = image,
                    faceOverlays = faceDescriptors.map { it.region.asFace(image) },
                    faceHighlightCornerRadius = 4.dp,
                    faceHighlightColor = Color.Yellow,
                    faceHighlightThickness = 2.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(350.dp)
                )

                SearchAttributesLayout(
                    searchAttributes = searchAttributes,
                    modifier.padding(8.dp)
                )
            }
        }
    }
}

private fun FaceDescriptor.Region.asFace(originalImage: Bitmap): FaceOverlay {
    return FaceOverlay(
        left = originalImage.width * left,
        top = originalImage.height * top,
        right = originalImage.width * (left + width),
        bottom = originalImage.height * (top + height),
    )
}
