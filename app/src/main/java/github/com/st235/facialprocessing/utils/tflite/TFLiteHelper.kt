package github.com.st235.facialprocessing.utils.tflite

import android.content.Context
import android.graphics.Bitmap
import androidx.annotation.RawRes
import github.com.st235.facialprocessing.utils.asByteArray
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder

inline fun Bitmap.writeToByteBuffer(
    byteBuffer: ByteBuffer,
    pixelProcessingPredicate: (value: Int) -> Float = { it.toFloat() }
) {
    byteBuffer.rewind()

    val pixels = IntArray(width * height)
    getPixels(pixels, 0, width, 0, 0, width, height)

    for (i in 0 until width) {
        for (j in 0 until height) {
            val rawPixelValue = pixels[(i * width) + j]

            byteBuffer.putFloat(pixelProcessingPredicate(rawPixelValue and 0xFF))
            byteBuffer.putFloat(pixelProcessingPredicate(rawPixelValue shr 8 and 0xFF))
            byteBuffer.putFloat(pixelProcessingPredicate(rawPixelValue shr 16 and 0xFF))
        }
    }
}

fun createDataByteBuffer(capacity: Int): ByteBuffer {
    val buffer = ByteBuffer.allocateDirect(capacity)
    buffer.order(ByteOrder.nativeOrder())
    return buffer
}

@Throws(IOException::class)
fun loadModelFromRawResources(
    context: Context,
    @RawRes rawResourceId: Int
): ByteBuffer {
    val inputStream = context.resources.openRawResource(rawResourceId)
    return inputStream.use {
        val byteArray = inputStream.asByteArray()
        val buffer = createDataByteBuffer(byteArray.size)
        buffer.put(byteArray)
    }
}
