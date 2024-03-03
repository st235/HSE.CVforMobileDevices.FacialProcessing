package github.com.st235.facialprocessing.presentation.screens.details

import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import github.com.st235.facialprocessing.R
import github.com.st235.facialprocessing.domain.model.FaceDescriptor
import github.com.st235.facialprocessing.interactors.models.FaceCluster
import github.com.st235.facialprocessing.interactors.models.FaceSearchAttribute
import github.com.st235.facialprocessing.presentation.theme.brightActiveFaceHighlightColor
import github.com.st235.facialprocessing.presentation.theme.brightInactiveFaceHighlightColor
import github.com.st235.facialprocessing.presentation.theme.facePreviewAreaBackground
import github.com.st235.facialprocessing.presentation.widgets.FaceClusterView
import github.com.st235.facialprocessing.presentation.widgets.FaceOverlay
import github.com.st235.facialprocessing.presentation.widgets.FaceView
import github.com.st235.facialprocessing.presentation.widgets.SearchAttributesLayout
import github.com.st235.facialprocessing.utils.textRes

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun DetailsScreen(
    mediaId: Long,
    clusterId: Int?,
    viewModel: DetailsViewModel,
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(true) {
        viewModel.loadState(mediaId, clusterId)
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
            val selectedFaceId = state.selectedFaceId
            val selectedFaceCluster = state.selectedFaceCluster
            val faceDescriptors = state.faceDescriptors
            val imageSearchAttributes = state.searchAttributes

            val faceOverlays = faceDescriptors.map { (id, faceToDescriptorToSearchAttributes) ->
                return@map faceToDescriptorToSearchAttributes.first.asFace(id = id, image)
            }

            Column(
                modifier = Modifier
                    .padding(paddings)
                    .verticalScroll(rememberScrollState())
            ) {
                FaceView(
                    image = image,
                    selectedOverlayId = selectedFaceId,
                    faceOverlays = faceOverlays,
                    faceHighlightThickness = 4.dp,
                    faceActiveColor = brightActiveFaceHighlightColor,
                    faceInactiveColor = brightInactiveFaceHighlightColor,
                    onFaceSelected = { viewModel.selectFace(it.id) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp)
                        .background(facePreviewAreaBackground)
                )

                if (selectedFaceId != null) {
                    val (descriptor, searchAttributes) = faceDescriptors.getValue(selectedFaceId)

                    FaceDescriptionView(
                        faceDescriptor = descriptor,
                        faceCluster = selectedFaceCluster,
                        faceSearchAttributes = searchAttributes,
                        modifier = Modifier.padding(16.dp)
                    )
                } else {
                    ImageDescriptionView(
                        imageSearchAttributes = imageSearchAttributes,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ImageDescriptionView(
    imageSearchAttributes: Set<FaceSearchAttribute.Type>,
    modifier: Modifier = Modifier,
    textColor: Color = MaterialTheme.colorScheme.onBackground,
    onSearchAttributeClick: (FaceSearchAttribute.Type) -> Unit = {},
) {
    Column(modifier = modifier) {
        Text(
            stringResource(R.string.detailed_screen_image_description_title),
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
            color = textColor,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            stringResource(R.string.detailed_screen_image_description_description),
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            color = textColor,
        )
        Spacer(modifier = Modifier.height(16.dp))
        SearchAttributesLayout(
            searchAttributes = imageSearchAttributes,
            onSearchAttributeClick = onSearchAttributeClick,
        )
    }
}

@Composable
private fun FaceDescriptionView(
    faceDescriptor: FaceDescriptor,
    faceCluster: FaceCluster?,
    faceSearchAttributes: Set<FaceSearchAttribute.Type>,
    modifier: Modifier = Modifier,
    textColor: Color = MaterialTheme.colorScheme.onBackground,
    onClusterClick: (Int) -> Unit = {},
    onSearchAttributeClick: (FaceSearchAttribute.Type) -> Unit = {},
) {
    val genderText = stringResource(faceDescriptor.gender.textRes)
    val emotionText = stringResource(faceDescriptor.emotion.textRes)
    val attributesText = faceDescriptor.attributes.map { stringResource(it.textRes) }

    Column(modifier = modifier) {
        Text(
            stringResource(R.string.detailed_screen_face_description_title),
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
            color = textColor,
        )
        if (faceCluster != null) {
            Spacer(modifier = Modifier.height(2.dp))
            FaceClusterView(
                face = faceCluster.sampleFace,
                text = stringResource(R.string.detailed_screen_face_description_cluster),
                onClick = { onClusterClick(faceCluster.clusterId) }
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            stringResource(R.string.detailed_screen_face_description_age, faceDescriptor.age),
            fontWeight = FontWeight.Normal,
            fontSize = 20.sp,
            color = textColor,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            stringResource(R.string.detailed_screen_face_description_gender, genderText),
            fontWeight = FontWeight.Normal,
            fontSize = 20.sp,
            color = textColor,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            stringResource(R.string.detailed_screen_face_description_mood, emotionText),
            fontWeight = FontWeight.Normal,
            fontSize = 20.sp,
            color = textColor,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            stringResource(R.string.detailed_screen_face_description_attributes, attributesText.joinToString(separator = ", ")),
            fontWeight = FontWeight.Normal,
            fontSize = 20.sp,
            color = textColor,
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            stringResource(R.string.detailed_screen_face_description_tags),
            fontWeight = FontWeight.Medium,
            fontSize = 22.sp,
            color = textColor,
        )
        Spacer(modifier = Modifier.height(8.dp))
        SearchAttributesLayout(
            searchAttributes = faceSearchAttributes,
            onSearchAttributeClick = onSearchAttributeClick
        )
    }
}

private fun FaceDescriptor.asFace(id: Int, originalImage: Bitmap): FaceOverlay {
    return FaceOverlay(
        id = id,
        left = originalImage.width * region.left,
        top = originalImage.height * region.top,
        right = originalImage.width * (region.left + region.width),
        bottom = originalImage.height * (region.top + region.height),
    )
}
