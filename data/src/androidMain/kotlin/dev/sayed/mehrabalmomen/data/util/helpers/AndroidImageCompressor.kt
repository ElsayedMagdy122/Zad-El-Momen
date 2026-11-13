package dev.sayed.mehrabalmomen.data.util.helpers

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.core.graphics.scale
import androidx.core.net.toUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import kotlin.math.max
import kotlin.math.min

class AndroidImageCompressor(
    private val context: Context
) : ImageCompressor {

    override suspend fun compressFromUri(
        uriString: String,
        maxWidth: Int,
        maxHeight: Int,
        quality: Int
    ): ByteArray? = withContext(Dispatchers.IO) {
        try {
            val uri = uriString.toUri()
            context.contentResolver.openInputStream(uri)?.use { input ->
                val originalBytes = input.readBytes()
                compress(originalBytes, maxWidth, maxHeight, quality)
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun compress(
        originalBytes: ByteArray,
        maxWidth: Int,
        maxHeight: Int,
        quality: Int
    ): ByteArray {
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
            BitmapFactory.decodeByteArray(originalBytes, 0, originalBytes.size, this)

            val heightRatio = outHeight / maxHeight
            val widthRatio = outWidth / maxWidth
            inSampleSize = max(1, max(heightRatio, widthRatio))

            inJustDecodeBounds = false
        }

        var bitmap = BitmapFactory.decodeByteArray(originalBytes, 0, originalBytes.size, options)
            ?: throw IllegalStateException("Cannot decode bitmap")

        if (bitmap.width > maxWidth || bitmap.height > maxHeight) {
            val scale = min(
                maxWidth.toFloat() / bitmap.width,
                maxHeight.toFloat() / bitmap.height
            )
            bitmap = bitmap.scale((bitmap.width * scale).toInt(), (bitmap.height * scale).toInt())
        }

        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
        bitmap.recycle()

        return outputStream.toByteArray()
    }
}
