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
import github.com.st235.facialprocessing.domain.AgeExtractor
import github.com.st235.facialprocessing.domain.EmotionExtractor
import github.com.st235.facialprocessing.domain.FaceDetector
import github.com.st235.facialprocessing.domain.FacialAttributesExtractor
import github.com.st235.facialprocessing.domain.GenderExtractor
import github.com.st235.facialprocessing.domain.InterpreterFactory
import github.com.st235.facialprocessing.presentation.widgets.FaceOverlayPainter

@Composable
fun ClusteringFeed(
    viewModel: ClusteringViewModel,
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.bc)
    val interpreterFactory = InterpreterFactory(context)
    val detector = FaceDetector(R.raw.model_face_detection_media_pipe_full_range, interpreterFactory)
    val ageExtractor = AgeExtractor(interpreterFactory)
    val genderExtractor = GenderExtractor(interpreterFactory)
    val emotionExtractor = EmotionExtractor(interpreterFactory)
    val facialAttributesExtractor = FacialAttributesExtractor(interpreterFactory)

    val boxes by remember(true) {
        mutableStateOf(detector.detect(bitmap))
    }

    for (box in boxes) {
        val face = Bitmap.createBitmap(bitmap, box.xMin.toInt(), box.yMin.toInt(), box.width.toInt(), box.height.toInt())
        val age = ageExtractor.predict(face)
        val gender = genderExtractor.predict(face)
        val emotion = emotionExtractor.predict(face)
        val facialAttributes = facialAttributesExtractor.predict(face)
        Log.d("HelloWorld", "Age: $age, Gender $gender, Emotion $emotion, Facial Attributes: $facialAttributes")
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
