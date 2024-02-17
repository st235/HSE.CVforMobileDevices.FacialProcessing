package github.com.st235.facialprocessing.presentation.widgets

import androidx.annotation.Px
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.toSize
import kotlin.math.roundToInt

class FaceOverlayPainter(
    private val image: ImageBitmap,
    private val faces: List<Face>,
    @Px private val faceHighlightCornerRadiusPx: Float,
    private val faceHighlightColor: Color,
    @Px private val faceHighlightThickness: Float,
    private val srcOffset: IntOffset = IntOffset.Zero,
    private val srcSize: IntSize = IntSize(image.width, image.height)
) : Painter() {

    data class Face(
        val left: Float,
        val top: Float,
        val right: Float,
        val bottom: Float,
    )

    private fun Face.scale(scaleX: Float, scaleY: Float): Face {
        return Face(
            left / scaleX,
            top / scaleY,
            right / scaleX,
            bottom / scaleY,
        )
    }

    internal var filterQuality: FilterQuality = FilterQuality.Low

    private val size: IntSize = validateSize(srcOffset, srcSize)

    private var alpha: Float = 1.0f

    private var colorFilter: ColorFilter? = null

    override fun DrawScope.onDraw() {
        val destinationWidth = this@onDraw.size.width.roundToInt()
        val destinationHeight = this@onDraw.size.height.roundToInt()

        val scaleX = image.width / destinationWidth.toFloat()
        val scaleY = image.height / destinationHeight.toFloat()

        drawImage(
            image,
            srcOffset,
            srcSize,
            dstSize = IntSize(
                this@onDraw.size.width.roundToInt(),
                this@onDraw.size.height.roundToInt()
            ),
            alpha = alpha,
            colorFilter = colorFilter,
            filterQuality = filterQuality
        )

        for (face in faces.map { it.scale(scaleX, scaleY) }) {
            drawRoundRect(
                color = faceHighlightColor,
                topLeft = Offset(face.left, face.top),
                size = Size(face.right - face.left, face.bottom - face.top),
                cornerRadius = CornerRadius(faceHighlightCornerRadiusPx, faceHighlightCornerRadiusPx),
                style = Stroke(width = faceHighlightThickness)
            )
        }
    }

    /**
     * Return the dimension of the underlying [ImageBitmap] as it's intrinsic width and height
     */
    override val intrinsicSize: Size get() = size.toSize()

    override fun applyAlpha(alpha: Float): Boolean {
        this.alpha = alpha
        return true
    }

    override fun applyColorFilter(colorFilter: ColorFilter?): Boolean {
        this.colorFilter = colorFilter
        return true
    }

    private fun validateSize(srcOffset: IntOffset, srcSize: IntSize): IntSize {
        require(
            srcOffset.x >= 0 &&
                    srcOffset.y >= 0 &&
                    srcSize.width >= 0 &&
                    srcSize.height >= 0 &&
                    srcSize.width <= image.width &&
                    srcSize.height <= image.height
        )
        return srcSize
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is FaceOverlayPainter) return false

        if (image != other.image) return false
        if (srcOffset != other.srcOffset) return false
        if (srcSize != other.srcSize) return false
        return filterQuality == other.filterQuality
    }

    override fun hashCode(): Int {
        var result = image.hashCode()
        result = 31 * result + srcOffset.hashCode()
        result = 31 * result + srcSize.hashCode()
        result = 31 * result + filterQuality.hashCode()
        return result
    }

    override fun toString(): String {
        return "BitmapPainter(image=$image, srcOffset=$srcOffset, srcSize=$srcSize, " +
                "filterQuality=$filterQuality)"
    }
}
