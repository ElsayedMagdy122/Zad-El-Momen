package dev.sayed.mehrabalmomen.data.recitation.engine

import android.content.Context
import android.util.Log
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.storage.storage
import io.ktor.client.HttpClient
import io.ktor.client.request.prepareGet
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.contentLength
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.readAvailable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipInputStream

sealed class ModelDownloadState {
    object Idle : ModelDownloadState()
    data class Downloading(val progress: Float, val currentFile: String) : ModelDownloadState()
    data class Processing(val message: String) : ModelDownloadState()
    object Ready : ModelDownloadState()
    data class Error(val message: String) : ModelDownloadState()
}

/**
 * Enterprise-grade Remote Downloader for AI Recitation Models and Assets.
 *
 * Includes automatic integrity & corruption verification per file:
 * 1. Smart Zip extraction & split merge: Automatically unzips individual zip parts before concatenating raw binary slices.
 * 2. Strict Size Validation: Verifies that merged model meets full 72.7MB size threshold (72,700,000 bytes).
 * 3. Cache Reuse & Resiliency: Reuses cached files and handles HTTP 200 validation.
 * 4. Corruption Auto-healing: Automatically detects truncated/corrupted files, deletes them, and re-fetches them.
 * 5. Real-time streaming progress: Smooth 1% -> 100% updates.
 */
class RecitationModelDownloader(
    private val context: Context,
    private val supabase: SupabaseClient,
    private val httpClient: HttpClient
) {
    companion object {
        private const val TAG = "RecitationModelDownloader"
        private const val BUCKET_NAME = "recitation-models"
        private const val MERGED_MODEL_NAME = "zipformer_p_arabic_v3.1.int8.onnx"

        // Strict threshold: ONNX model MUST be at least 72,700,000 bytes (72.7 MB)
        private const val EXPECTED_MODEL_SIZE = 72705392L
        private const val MIN_VALID_MODEL_SIZE = 72700000L

        // Minimum valid file size in bytes for auxiliary assets
        private val REQUIRED_ASSETS_WITH_MIN_SIZE = mapOf(
            "tokens.txt" to 1000L,                     // ~2.5 KB
            "ordered_quran_phonemes.json" to 1000000L,  // ~1.5 MB
            "ph_index.npy" to 500000L,                 // ~1.0 MB
            "ref_norm_ph.txt" to 100L                  // ~1.0 KB
        )
    }

    private val _downloadState = MutableStateFlow<ModelDownloadState>(ModelDownloadState.Idle)
    val downloadState: Flow<ModelDownloadState> = _downloadState.asStateFlow()

    private fun isFileValid(file: File, minExpectedSize: Long): Boolean {
        return file.exists() && file.length() >= minExpectedSize
    }

    fun isModelReady(): Boolean {
        val mergedModel = File(context.filesDir, MERGED_MODEL_NAME)
        if (!isFileValid(mergedModel, MIN_VALID_MODEL_SIZE)) return false

        return REQUIRED_ASSETS_WITH_MIN_SIZE.all { (name, minSize) ->
            val file = File(context.filesDir, name)
            isFileValid(file, minSize)
        }
    }

    suspend fun downloadAndPrepareModels(): Boolean = withContext(Dispatchers.IO) {
        if (isModelReady()) {
            _downloadState.value = ModelDownloadState.Ready
            return@withContext true
        }

        try {
            Log.d(TAG, "🔄 [RecitationModelDownloader] Checking and downloading model files...")
            val targetModelFile = File(context.filesDir, MERGED_MODEL_NAME)
            val part1ZipFile = File(context.cacheDir, "model1.zip")
            val part2ZipFile = File(context.cacheDir, "model2.zip")
            val part1RawFile = File(context.cacheDir, "part1.raw")
            val part2RawFile = File(context.cacheDir, "part2.raw")

            // Step 1: Verify model integrity (If missing or corrupted, delete and re-download)
            if (!isFileValid(targetModelFile, MIN_VALID_MODEL_SIZE)) {
                if (targetModelFile.exists()) {
                    Log.w(TAG, "⚠️ Target model file is corrupted/incomplete (${targetModelFile.length()} bytes vs required $MIN_VALID_MODEL_SIZE). Re-downloading...")
                    targetModelFile.delete()
                }

                // Download part 1 if not cached
                if (!part1ZipFile.exists() || part1ZipFile.length() < 10000000L) {
                    Log.d(TAG, "📥 Streaming model1.zip...")
                    streamDownloadToFile(
                        fileName = "model1.zip",
                        outputFile = part1ZipFile,
                        baseProgress = 0.0f,
                        progressWeight = 0.40f
                    )
                }

                // Download part 2 if not cached
                if (!part2ZipFile.exists() || part2ZipFile.length() < 10000000L) {
                    try {
                        Log.d(TAG, "📥 Streaming model2.zip...")
                        streamDownloadToFile(
                            fileName = "model2.zip",
                            outputFile = part2ZipFile,
                            baseProgress = 0.40f,
                            progressWeight = 0.40f
                        )
                    } catch (e: Exception) {
                        Log.w(TAG, "model2.zip stream skipped or failed: ${e.message}")
                    }
                }

                _downloadState.value = ModelDownloadState.Processing("جاري فك ضغط وتجميع ملفات النموذج...")

                // Unzip part 1 if it's a zip archive
                val part1Unzipped = extractFileFromZip(part1ZipFile, part1RawFile)
                val sourcePart1 = if (part1Unzipped) part1RawFile else part1ZipFile

                // Unzip part 2 if it's a zip archive
                val part2Unzipped = extractFileFromZip(part2ZipFile, part2RawFile)
                val sourcePart2 = if (part2Unzipped) part2RawFile else part2ZipFile

                // Check if single part alone is complete
                if (sourcePart1.length() >= MIN_VALID_MODEL_SIZE) {
                    Log.d(TAG, "📦 Part 1 alone is a complete ONNX model (${sourcePart1.length()} bytes). Copying...")
                    sourcePart1.copyTo(targetModelFile, overwrite = true)
                } else if (sourcePart1.exists() && sourcePart2.exists() && sourcePart2.length() > 0L) {
                    // Concatenate unzipped raw part 1 + unzipped raw part 2
                    Log.d(TAG, "🧩 Merging unzipped raw parts (${sourcePart1.length()} + ${sourcePart2.length()} bytes) into $MERGED_MODEL_NAME...")
                    FileOutputStream(targetModelFile).use { out ->
                        sourcePart1.inputStream().use { in1 -> in1.copyTo(out) }
                        sourcePart2.inputStream().use { in2 -> in2.copyTo(out) }
                    }
                }

                // Cleanup temporary cache files
                if (part1ZipFile.exists()) part1ZipFile.delete()
                if (part2ZipFile.exists()) part2ZipFile.delete()
                if (part1RawFile.exists()) part1RawFile.delete()
                if (part2RawFile.exists()) part2RawFile.delete()

                Log.d(TAG, "✅ Target model processed (${targetModelFile.length()} bytes)")
            }

            // Step 2: Verify & Download Auxiliary Assets
            val assetEntries = REQUIRED_ASSETS_WITH_MIN_SIZE.entries.toList()
            val totalAssets = assetEntries.size

            assetEntries.forEachIndexed { index, (assetName, minSize) ->
                val assetFile = File(context.filesDir, assetName)
                if (!isFileValid(assetFile, minSize)) {
                    if (assetFile.exists()) {
                        Log.w(TAG, "⚠️ Asset $assetName is incomplete (${assetFile.length()} bytes). Re-downloading...")
                        assetFile.delete()
                    }

                    val baseProgress = 0.85f + (index.toFloat() / totalAssets.toFloat()) * 0.14f
                    val weight = 0.14f / totalAssets.toFloat()
                    Log.d(TAG, "📥 Streaming asset: $assetName...")
                    streamDownloadToFile(
                        fileName = assetName,
                        outputFile = assetFile,
                        baseProgress = baseProgress,
                        progressWeight = weight
                    )
                }
            }

            if (!isModelReady()) {
                val currentSize = targetModelFile.length()
                if (targetModelFile.exists()) targetModelFile.delete()
                throw IllegalStateException("ملف الموديل المحمل غير مكتمل ($currentSize بايت بدلاً من $EXPECTED_MODEL_SIZE بايت). يرجى التأكد من التنزيل بنجاح.")
            }

            _downloadState.value = ModelDownloadState.Processing("جاري تهيئة محرك الذكاء الاصطناعي...")
            _downloadState.value = ModelDownloadState.Ready
            Log.d(TAG, "🎉 [RecitationModelDownloader] All models & assets downloaded & verified successfully!")
            true
        } catch (e: Exception) {
            if (e is kotlinx.coroutines.CancellationException) {
                Log.d(TAG, "🛑 [RecitationModelDownloader] Download job cancelled by user.")
                _downloadState.value = ModelDownloadState.Idle
                throw e
            }
            Log.e(TAG, "❌ [RecitationModelDownloader] Download failed: ${e.message}", e)
            _downloadState.value = ModelDownloadState.Error(e.localizedMessage ?: "فشل تنزيل ملفات النموذج")
            false
        }
    }

    fun resetState() {
        _downloadState.value = ModelDownloadState.Idle
    }

    private fun extractFileFromZip(zipFile: File, outputFile: File): Boolean {
        if (!zipFile.exists() || zipFile.length() == 0L) return false
        try {
            ZipInputStream(zipFile.inputStream().buffered()).use { zipIn ->
                var entry = zipIn.nextEntry
                while (entry != null) {
                    if (!entry.isDirectory) {
                        Log.d(TAG, "📦 Unzipping entry '${entry.name}' (${entry.size} bytes)...")
                        FileOutputStream(outputFile).use { out ->
                            zipIn.copyTo(out)
                        }
                        Log.d(TAG, "✅ Unzipped entry size: ${outputFile.length()} bytes")
                        return outputFile.exists() && outputFile.length() > 0L
                    }
                    zipIn.closeEntry()
                    entry = zipIn.nextEntry
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "File is not a zip archive: ${e.message}")
        }
        return false
    }

    private suspend fun streamDownloadToFile(
        fileName: String,
        outputFile: File,
        baseProgress: Float,
        progressWeight: Float
    ) {
        val publicUrl = supabase.storage.from(BUCKET_NAME).publicUrl(fileName)
        val statement = httpClient.prepareGet(publicUrl)
        statement.execute { response ->
            if (response.status.value !in 200..299) {
                throw IllegalStateException("خطأ شبكة (HTTP ${response.status.value}) عند تنزيل $fileName")
            }
            val contentLength = response.contentLength() ?: -1L
            val channel: ByteReadChannel = response.bodyAsChannel()
            val buffer = ByteArray(16384)
            var fileDownloadedBytes = 0L

            FileOutputStream(outputFile).use { out ->
                while (!channel.isClosedForRead) {
                    val read = channel.readAvailable(buffer, 0, buffer.size)
                    if (read <= 0) break
                    out.write(buffer, 0, read)
                    fileDownloadedBytes += read

                    if (contentLength > 0L) {
                        val fileProgress = fileDownloadedBytes.toFloat() / contentLength.toFloat()
                        val overallProgress = (baseProgress + (fileProgress * progressWeight)).coerceIn(0f, 1f)
                        _downloadState.value = ModelDownloadState.Downloading(
                            progress = overallProgress,
                            currentFile = "جاري تنزيل نموذج التعرف الصوتي..."
                        )
                    }
                }
            }
        }
    }
}
