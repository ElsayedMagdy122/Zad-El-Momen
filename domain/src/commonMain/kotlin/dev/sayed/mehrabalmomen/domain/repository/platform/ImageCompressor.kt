package dev.sayed.mehrabalmomen.domain.repository.platform

/**
 * Platform-agnostic contract for reading and compressing image data from a URI or file path.
 */
interface ImageCompressor {
    suspend fun compressFromUri(uriString: String): ByteArray?
}
