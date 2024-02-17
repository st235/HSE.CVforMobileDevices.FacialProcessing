package github.com.st235.facialprocessing.presentation.screens.clustering_feed

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import github.com.st235.facialprocessing.R
import github.com.st235.facialprocessing.domain.FaceDetector
import github.com.st235.facialprocessing.presentation.widgets.FaceOverlayPainter

@Composable
fun ClusteringFeed(
    viewModel: ClusteringViewModel,
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.bc)

    val boxes by remember(true) {
        val detector = FaceDetector(context, R.raw.media_pipe_face_detection_full_range)
        mutableStateOf(detector.detect(bitmap))
    }

    Image(painter = FaceOverlayPainter(image = bitmap.asImageBitmap(), faces = boxes.map { it.asFace() }, faceHighlightCornerRadiusPx = 64f, faceHighlightColor = Color.Yellow, faceHighlightThickness = 8f), contentDescription = null,
        contentScale = ContentScale.Fit)
}

private fun FaceDetector.Box.asFace(): FaceOverlayPainter.Face {
    return FaceOverlayPainter.Face(
        left = xMin,
        top = yMin,
        right = xMax,
        bottom = yMax,
    )
}
