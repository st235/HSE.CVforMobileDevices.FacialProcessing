package github.com.st235.facialprocessing.presentation.screens.clustering_feed

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
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
import github.com.st235.facialprocessing.domain.model.FaceDescriptor
import github.com.st235.facialprocessing.domain.faces.FaceProcessor
import github.com.st235.facialprocessing.utils.tflite.InterpreterFactory
import github.com.st235.facialprocessing.presentation.widgets.FaceOverlayPainter

@Composable
fun ClusteringFeed(
    viewModel: ClusteringViewModel,
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
}

private fun FaceDescriptor.Region.asFace(originalImage: Bitmap): FaceOverlayPainter.Face {
    return FaceOverlayPainter.Face(
        left = originalImage.width * left,
        top = originalImage.height * top,
        right = originalImage.width * (left + width),
        bottom = originalImage.height * (top + height),
    )
}
