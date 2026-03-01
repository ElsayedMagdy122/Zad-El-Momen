package dev.sayed.mehrabalmomen.data.util.helpers

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.ByteArrayOutputStream
import kotlin.math.max
import kotlin.math.min
import androidx.core.graphics.scale

object ImageCompressor {

  private  fun compress(
        originalBytes: ByteArray,
        maxWidth: Int = 1280,
        maxHeight: Int = 1280,
        quality: Int = 80
    ): ByteArray {
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
            BitmapFactory.decodeByteArray(originalBytes, 0, originalBytes.size, this)

            val heightRatio = outHeight / maxHeight
            val widthRatio  = outWidth  / maxWidth
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

    fun compressFromUri(
        context: Context,
        uri: Uri,
        maxWidth: Int = 1280,
        maxHeight: Int = 1280,
        quality: Int = 80
    ): ByteArray {
        context.contentResolver.openInputStream(uri)?.use { input ->
            val originalBytes = input.readBytes()
            return compress(originalBytes, maxWidth, maxHeight, quality)
        } ?: throw IllegalStateException("Cannot open input stream")
    }
}