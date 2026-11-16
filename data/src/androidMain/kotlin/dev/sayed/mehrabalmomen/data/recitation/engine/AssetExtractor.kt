package dev.sayed.mehrabalmomen.data.recitation.engine

import android.content.Context
import java.io.File
import java.io.FileOutputStream

object AssetExtractor {
    fun extractAssetIfNeeded(context: Context, assetName: String): String {
        val outFile = File(context.filesDir, assetName)

        if (outFile.exists() && outFile.length() > 0L) {
            return outFile.absolutePath
        }

        try {
            context.assets.open(assetName).use { input ->
                FileOutputStream(outFile).use { output ->
                    input.copyTo(output)
                }
            }
        } catch (_: Exception) {
        }

        return outFile.absolutePath
    }
}
