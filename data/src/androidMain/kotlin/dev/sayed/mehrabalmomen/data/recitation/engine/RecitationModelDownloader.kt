package dev.sayed.mehrabalmomen.data.recitation.engine

import android.content.Context
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.storage.storage
import io.ktor.client.HttpClient
import io.ktor.client.request.prepareGet
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.contentLength
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.readAvailable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipFile
import java.util.zip.ZipInputStream

actual class RecitationModelDownloader(
    private val context: Context,
    private val supabase: SupabaseClient,
    private val httpClient: HttpClient
) {
    companion object {
        private const val BUCKET_NAME = "recitation-models"
        private const val MERGED_MODEL_NAME = "zipformer_p_arabic_v3.1.int8.onnx"

        private const val EXPECTED_MODEL_SIZE = 72705392L
        private const val MIN_VALID_MODEL_SIZE = 72700000L

        private val REQUIRED_ASSETS_WITH_MIN_SIZE = mapOf(
            "tokens.txt" to 1000L,
            "ordered_quran_phonemes.json" to 1000000L,
            "ph_index.npy" to 500000L,
            "ref_norm_ph.txt" to 100L
        )
    }

    private val _downloadState = MutableStateFlow<ModelDownloadState>(ModelDownloadState.Idle)
    actual val downloadState: StateFlow<ModelDownloadState> = _downloadState.asStateFlow()

    private val downloadMutex = Mutex()

    actual fun isDownloading(): Boolean {
        val current = _downloadState.value
        return current is ModelDownloadState.Downloading || current is ModelDownloadState.Processing
    }

    private fun isFileValid(file: File, minExpectedSize: Long): Boolean {
        return file.exists() && file.length() >= minExpectedSize
    }

    private fun isZipFileValid(file: File): Boolean {
        if (!file.exists() || file.length() < 1024L) return false
        return try {
            ZipFile(file).use { zip ->
                zip.entries().hasMoreElements()
            }
        } catch (_: Exception) {
            false
        }
    }

    actual fun isModelReady(): Boolean {
        val mergedModel = File(context.filesDir, MERGED_MODEL_NAME)
        if (!isFileValid(mergedModel, MIN_VALID_MODEL_SIZE)) return false

        return REQUIRED_ASSETS_WITH_MIN_SIZE.all { (name, minSize) ->
            val internalFile = File(context.filesDir, name)
            if (isFileValid(internalFile, minSize)) {
                true
            } else {
                try {
                    context.assets.open(name).use { input ->
                        input.available() >= minSize
                    }
                } catch (_: Exception) {
                    false
                }
            }
        }
    }

    actual suspend fun downloadAndPrepareModels(): Boolean = withContext(Dispatchers.IO) {
        if (isModelReady()) {
            _downloadState.value = ModelDownloadState.Ready
            return@withContext true
        }

        downloadMutex.withLock {
            if (isModelReady()) {
                _downloadState.value = ModelDownloadState.Ready
                return@withContext true
            }

            try {
                val targetModelFile = File(context.filesDir, MERGED_MODEL_NAME)
                val partsDir = File(context.filesDir, "recitation_parts").apply { mkdirs() }
                val part1ZipFile = File(partsDir, "model1.zip")
                val part2ZipFile = File(partsDir, "model2.zip")
                val part1RawFile = File(partsDir, "part1.raw")
                val part2RawFile = File(partsDir, "part2.raw")

                if (!isFileValid(targetModelFile, MIN_VALID_MODEL_SIZE)) {
                    if (targetModelFile.exists()) {
                        targetModelFile.delete()
                    }

                    if (!isZipFileValid(part1ZipFile)) {
                        if (part1ZipFile.exists()) part1ZipFile.delete()
                        streamDownloadToFile(
                            fileName = "model1.zip",
                            outputFile = part1ZipFile,
                            baseProgress = 0.0f,
                            progressWeight = 0.40f
                        )
                    }

                    if (!isZipFileValid(part2ZipFile)) {
                        if (part2ZipFile.exists()) part2ZipFile.delete()
                        try {
                            streamDownloadToFile(
                                fileName = "model2.zip",
                                outputFile = part2ZipFile,
                                baseProgress = 0.40f,
                                progressWeight = 0.40f
                            )
                        } catch (_: Exception) {
                        }
                    }

                    _downloadState.value = ModelDownloadState.Processing("جاري تجميع وفك ضغط ملفات نموذج التسميع...")

                    val part1Unzipped = extractFileFromZip(part1ZipFile, part1RawFile)
                    val sourcePart1 = if (part1Unzipped) part1RawFile else part1ZipFile

                    val part2Unzipped = extractFileFromZip(part2ZipFile, part2RawFile)
                    val sourcePart2 = if (part2Unzipped) part2RawFile else part2ZipFile

                    if (sourcePart1.length() >= MIN_VALID_MODEL_SIZE) {
                        sourcePart1.copyTo(targetModelFile, overwrite = true)
                    } else if (sourcePart1.exists() && sourcePart2.exists() && sourcePart2.length() > 0L) {
                        FileOutputStream(targetModelFile).use { out ->
                            sourcePart1.inputStream().use { in1 -> in1.copyTo(out) }
                            sourcePart2.inputStream().use { in2 -> in2.copyTo(out) }
                        }
                    }

                    if (!isFileValid(targetModelFile, MIN_VALID_MODEL_SIZE)) {
                        if (targetModelFile.exists()) targetModelFile.delete()
                        throw IllegalStateException("فشل فك وتجميع ملف النموذج الرئيسي (${targetModelFile.length()} بايت)")
                    }

                    if (part1ZipFile.exists()) part1ZipFile.delete()
                    if (part2ZipFile.exists()) part2ZipFile.delete()
                    if (part1RawFile.exists()) part1RawFile.delete()
                    if (part2RawFile.exists()) part2RawFile.delete()
                }

                val assetEntries = REQUIRED_ASSETS_WITH_MIN_SIZE.entries.toList()
                val totalAssets = assetEntries.size

                assetEntries.forEachIndexed { index, (assetName, minSize) ->
                    val assetFile = File(context.filesDir, assetName)
                    if (!isFileValid(assetFile, minSize)) {
                        val extracted = try {
                            context.assets.open(assetName).use { input ->
                                FileOutputStream(assetFile).use { output ->
                                    input.copyTo(output)
                                }
                            }
                            isFileValid(assetFile, minSize)
                        } catch (_: Exception) {
                            false
                        }

                        if (!extracted) {
                            if (assetFile.exists()) {
                                assetFile.delete()
                            }

                            val baseProgress = 0.85f + (index.toFloat() / totalAssets.toFloat()) * 0.14f
                            val weight = 0.14f / totalAssets.toFloat()
                            streamDownloadToFile(
                                fileName = assetName,
                                outputFile = assetFile,
                                baseProgress = baseProgress,
                                progressWeight = weight
                            )
                        }
                    }
                }

                if (!isModelReady()) {
                    throw IllegalStateException("بعض ملفات مراجعة الحفظ المساعدة غير مكتملة. يرجى إعادة المحاولة.")
                }

                _downloadState.value = ModelDownloadState.Processing("جاري تهيئة نموذج الذكاء الاصطناعي...")
                _downloadState.value = ModelDownloadState.Ready
                true
            } catch (e: Exception) {
                if (e is kotlinx.coroutines.CancellationException) {
                    _downloadState.value = ModelDownloadState.Idle
                    throw e
                }
                _downloadState.value = ModelDownloadState.Error(e.localizedMessage ?: "حدث خطأ أثناء تنزيل النماذج")
                false
            }
        }
    }

    actual fun resetState() {
        _downloadState.value = ModelDownloadState.Idle
    }

    private fun extractFileFromZip(zipFile: File, outputFile: File): Boolean {
        if (!zipFile.exists() || zipFile.length() == 0L) return false
        try {
            ZipInputStream(zipFile.inputStream().buffered()).use { zipIn ->
                var entry = zipIn.nextEntry
                while (entry != null) {
                    if (!entry.isDirectory) {
                        FileOutputStream(outputFile).use { out ->
                            zipIn.copyTo(out)
                        }
                        return outputFile.exists() && outputFile.length() > 0L
                    }
                    zipIn.closeEntry()
                    entry = zipIn.nextEntry
                }
            }
        } catch (_: Exception) {
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
