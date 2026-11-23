package dev.sayed.mehrabalmomen.data.platform.system

import android.content.Context
import androidx.core.net.toUri
import dev.sayed.mehrabalmomen.data.util.helpers.ImageCompressor as HelperCompressor
import dev.sayed.mehrabalmomen.domain.repository.platform.ImageCompressor

class AndroidImageCompressor(
    private val context: Context
) : ImageCompressor {
    override suspend fun compressFromUri(uriString: String): ByteArray? {
        return try {
            val uri = uriString.toUri()
            HelperCompressor.compressFromUri(
                context = context,
                uri = uri,
                maxWidth = 1280,
                maxHeight = 1280,
                quality = 78
            )
        } catch (_: Exception) {
            null
        }
    }
}
