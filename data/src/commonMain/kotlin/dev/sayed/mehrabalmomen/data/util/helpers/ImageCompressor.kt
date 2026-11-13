package dev.sayed.mehrabalmomen.data.util.helpers

/**
 * Interface for compressing images before uploading.
 */
interface ImageCompressor {
    suspend fun compressFromUri(
        uriString: String,
        maxWidth: Int = 1280,
        maxHeight: Int = 1280,
        quality: Int = 78
    ): ByteArray?
}
