package dev.sayed.mehrabalmomen.data.util

/**
 * Multiplatform abstraction for reading asset files (e.g. JSON datasets).
 */
interface AssetReader {
    suspend fun readText(fileName: String): String
}
