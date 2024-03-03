package github.com.st235.facialprocessing.presentation.widgets

import android.graphics.Bitmap
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class FaceOverlay(
    val id: Int,
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float,
)

private fun FaceOverlay.scale(scaleX: Float, scaleY: Float): FaceOverlay {
    return FaceOverlay(
        id = id,
        left = left / scaleX,
        top = top / scaleY,
        right = right / scaleX,
        bottom = bottom / scaleY,
    )
}

private fun FaceOverlay.offset(offset: Offset): FaceOverlay {
    return FaceOverlay(
        id = id,
        left = offset.x + left,
        top = offset.y + top,
        right = offset.x + right,
        bottom = offset.y + bottom,
    )
}

private fun FaceOverlay.inside(offset: Offset): Boolean {
    return (offset.x in left..right) && (offset.y in top..bottom)
}

@Composable
fun FaceView(
    image: Bitmap,
    faceOverlays: List<FaceOverlay>,
    modifier: Modifier = Modifier,
    selectedOverlayId: Int? = null,
    faceActiveColor: Color = MaterialTheme.colorScheme.primary,
    faceInactiveColor: Color = MaterialTheme.colorScheme.outline,
    faceHighlightCornerRadius: Dp = 8.dp,
    faceHighlightThickness: Dp = 2.dp,
    onFaceSelected: (FaceOverlay) -> Unit = {},
    onFaceDeselected: (FaceOverlay) -> Unit = {},
) {
    var scaledFaceOverlays by remember { mutableStateOf<List<FaceOverlay>>(emptyList()) }

    val faceHighlightCornerRadiusPx = with(LocalDensity.current) { faceHighlightCornerRadius.toPx() }
    val faceHighlightThicknessPx = with(LocalDensity.current) { faceHighlightThickness.toPx() }

    Canvas(
        modifier = modifier
            .focusable()
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val selectedOverlay = scaledFaceOverlays.find { it.inside(offset) }
                    if (selectedOverlay != null) {
                        if (selectedOverlay.id != selectedOverlayId) {
                            onFaceSelected(selectedOverlay)
                        } else {
                            onFaceDeselected(selectedOverlay)
                        }
                    }
                }
            }
    ) {
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

        scaledFaceOverlays = faceOverlays.map {
            it.scale(scaleFactor, scaleFactor).offset(Offset(offsetX, offsetY))
        }

        for (face in scaledFaceOverlays) {
            val isFaceSelected = face.id == selectedOverlayId
            val highlightColor = if (isFaceSelected) {
                faceActiveColor
            } else {
                faceInactiveColor
            }

            drawRoundRect(
                color = highlightColor,
                topLeft = Offset(face.left, face.top),
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
