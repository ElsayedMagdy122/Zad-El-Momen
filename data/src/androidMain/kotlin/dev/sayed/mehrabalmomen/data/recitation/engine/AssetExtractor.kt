package dev.sayed.mehrabalmomen.data.recitation.engine

import android.content.Context
import java.io.File
import java.io.FileOutputStream

object AssetExtractor {
    fun extractAssetIfNeeded(context: Context, assetName: String): String {
        val outFile = File(context.filesDir, assetName)

        // Priority 1: File already exists in internal storage (e.g. downloaded from Supabase)
        if (outFile.exists() && outFile.length() > 0L) {
            return outFile.absolutePath
        }

        // Priority 2: Fallback to extracting from APK assets if available
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
