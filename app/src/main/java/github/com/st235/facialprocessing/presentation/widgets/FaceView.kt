package github.com.st235.facialprocessing.presentation.widgets

import android.graphics.Bitmap
import androidx.annotation.Px
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp

data class FaceOverlay(
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float,
)

fun FaceOverlay.scale(scaleX: Float, scaleY: Float): FaceOverlay {
    return FaceOverlay(
        left / scaleX,
        top / scaleY,
        right / scaleX,
        bottom / scaleY,
    )
}

@Composable
fun FaceView(
    image: Bitmap,
    faceOverlays: List<FaceOverlay>,
    faceHighlightCornerRadius: Dp,
    faceHighlightColor: Color,
    faceHighlightThickness: Dp,
    modifier: Modifier = Modifier,
) {
    val faceHighlightCornerRadiusPx = with(LocalDensity.current) { faceHighlightCornerRadius.toPx() }
    val faceHighlightThicknessPx = with(LocalDensity.current) { faceHighlightThickness.toPx() }

    Canvas(modifier = modifier) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        val imageWidth = image.width.toFloat()
        val imageHeight = image.height.toFloat()

        val scaleFactor = calculateScaleToFitFactor(imageWidth, imageHeight, canvasWidth, canvasHeight)

        val newWidth = imageWidth / scaleFactor
        val newHeight = imageHeight / scaleFactor

        val previewBitmap = Bitmap.createScaledBitmap(
            image,
            newWidth.toInt(),
            newHeight.toInt(),
            false
        )

        val offsetX = (canvasWidth - previewBitmap.width) / 2
        val offsetY = (canvasHeight - previewBitmap.height) / 2

        drawImage(
            previewBitmap.asImageBitmap(),
            topLeft = Offset(
                offsetX,
                offsetY,
            )
        )

        for (face in faceOverlays.map { it.scale(scaleFactor, scaleFactor) }) {
            drawRoundRect(
                color = faceHighlightColor,
                topLeft = Offset(offsetX + face.left, offsetY + face.top),
                size = Size(face.right - face.left, face.bottom - face.top),
                cornerRadius = CornerRadius(faceHighlightCornerRadiusPx, faceHighlightCornerRadiusPx),
                style = Stroke(width = faceHighlightThicknessPx)
            )
        }
    }
}

/**
 * Returns image to canvas ratio.
 * To get new image sizes, one needs to divide the
 * real image dimension to this ratio.
 *
 * newWidth = imageWidth / scaleFactor
 * newHeight = imageHeight / scaleFactor
 */
private fun calculateScaleToFitFactor(
    imageWidth: Float, imageHeight: Float,
    canvasWidth: Float, canvasHeight: Float
): Float {
    val factor = imageWidth / canvasWidth

    if (imageHeight / factor <= canvasHeight) {
        return factor
    }

    return imageHeight / canvasHeight
}
