package github.com.st235.facialprocessing.utils

import java.io.ByteArrayOutputStream
import java.io.InputStream

const val DEFAULT_BYTE_BUFFER_SIZE = 16384

fun InputStream.asByteArray(): ByteArray {
    val buffer = ByteArrayOutputStream()

    val data = ByteArray(DEFAULT_BYTE_BUFFER_SIZE)

    var nRead = read(data, 0, data.size)
    while (nRead != -1) {
        buffer.write(data, 0, nRead)
        nRead = read(data, 0, data.size)
    }
    return buffer.toByteArray()
}
