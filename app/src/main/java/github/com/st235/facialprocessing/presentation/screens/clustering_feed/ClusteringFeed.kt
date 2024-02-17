package github.com.st235.facialprocessing.presentation.screens.clustering_feed

import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import github.com.st235.facialprocessing.R
import github.com.st235.facialprocessing.domain.FaceDetector
import java.util.Collections

@Composable
fun ClusteringFeed(
    viewModel: ClusteringViewModel,
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    LaunchedEffect("view_model") {
//        viewModel.loadAllPhotos()
        val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.bc)
        val detector = FaceDetector(context, R.raw.media_pipe_face_detection_full_range)

        val boxes = detector.detect(bitmap)
        for (box in boxes) {
            Log.d("HelloWorld", "[${box.xMin}, ${box.yMin}, ${box.xMax}, ${box.yMax}]")
        }
    }

    Text("Hello world!")
}
