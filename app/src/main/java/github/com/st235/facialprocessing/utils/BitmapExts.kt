package github.com.st235.facialprocessing.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint


fun Bitmap.scaleLetterBox(newWidth: Int,
                          newHeight: Int,
                          normalisedPaddings: FloatArray): Bitmap {
    val background = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.RGB_565)

    val originalWidth: Float = width.toFloat()
    val originalHeight: Float = height.toFloat()

    val canvas = Canvas(background)

    val scaledWidth: Int
    val scaledHeight: Int

    if (width > height) {
        scaledWidth = newWidth
        scaledHeight = ((newWidth * height) / originalWidth).toInt()
    } else {
        scaledHeight = newHeight
        scaledWidth = ((scaledHeight * width) / originalHeight).toInt()
    }

    val offsetX = (newWidth - scaledWidth) / 2f
    val offsetY = (newHeight - scaledHeight) / 2f

    val transformation = Matrix()
    transformation.preScale(scaledWidth.toFloat() / width, scaledHeight.toFloat() / height)
    transformation.postTranslate(offsetX, offsetY)

    val paint = Paint()
    paint.isFilterBitmap = true

    canvas.drawBitmap(this, transformation, paint)

    val normalisedOffsetX = offsetX / newWidth
    val normalisedOffsetY = offsetY / newHeight

    normalisedPaddings[0] = normalisedOffsetX
    normalisedPaddings[1] = normalisedOffsetY
    normalisedPaddings[2] = normalisedOffsetX
    normalisedPaddings[3] = normalisedOffsetY
    return background
}
