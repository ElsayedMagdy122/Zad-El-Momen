package dev.sayed.mehrabalmomen.data.recitation.engine

import android.content.Context
import android.util.Log
import java.io.File
import java.io.FileOutputStream

object AssetExtractor {
    private const val TAG = "AssetExtractor"

    fun extractAssetIfNeeded(context: Context, assetName: String): String {
        val outFile = File(context.filesDir, assetName)

        // Priority 1: File already exists in internal storage (e.g. downloaded from Supabase)
        if (outFile.exists() && outFile.length() > 0L) {
            Log.d(TAG, "📂 Using existing internal file for $assetName: ${outFile.absolutePath} (${outFile.length()} bytes)")
            return outFile.absolutePath
        }

        // Priority 2: Fallback to extracting from APK assets if available
        try {
            Log.d(TAG, "📂 Extracting $assetName from APK assets to internal storage...")
            context.assets.open(assetName).use { input ->
                FileOutputStream(outFile).use { output ->
                    input.copyTo(output)
                }
            }
            Log.d(TAG, "✅ Extracted $assetName to ${outFile.absolutePath}")
        } catch (e: Exception) {
            Log.w(TAG, "⚠️ Asset $assetName not found in APK assets: ${e.message}")
        }

        return outFile.absolutePath
    }
}
