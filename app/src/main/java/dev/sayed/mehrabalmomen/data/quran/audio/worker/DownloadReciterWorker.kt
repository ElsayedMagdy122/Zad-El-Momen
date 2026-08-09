package dev.sayed.mehrabalmomen.data.quran.audio.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import dev.sayed.mehrabalmomen.data.quran.audio.local.dao.DownloadedReciterDao
import dev.sayed.mehrabalmomen.data.quran.audio.local.entity.DownloadedReciterEntity
import dev.sayed.mehrabalmomen.data.quran.audio.local.entity.VerseTimingEntity
import dev.sayed.mehrabalmomen.data.quran.audio.remote.QuranAudioTimingsRemoteDataSource
import io.ktor.client.HttpClient
import io.ktor.client.plugins.timeout
import io.ktor.client.request.prepareGet
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.contentLength
import io.ktor.utils.io.readAvailable
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File
import kotlin.coroutines.cancellation.CancellationException
import kotlin.system.measureTimeMillis

class DownloadReciterWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {

    private val httpClient: HttpClient by inject()
    private val downloadedReciterDao: DownloadedReciterDao by inject()
    private val timingsRemoteDataSource: QuranAudioTimingsRemoteDataSource by inject()

    override suspend fun doWork(): Result {
        val reciterId = inputData.getInt(KEY_RECITER_ID, -1)
        val surahId = inputData.getInt(KEY_SURAH_ID, -1)
        val url = inputData.getString(KEY_URL)
        val nameAr = inputData.getString(KEY_NAME_AR) ?: ""
        val nameEn = inputData.getString(KEY_NAME_EN) ?: ""
        val rewayaName = inputData.getString(KEY_REWAYA_NAME) ?: ""
        val baseAudioUrl = inputData.getString(KEY_BASE_AUDIO_URL) ?: ""

        if (reciterId == -1 || surahId == -1 || url == null) {
            return Result.failure()
        }

        // Immediate feedback
        setProgress(workDataOf(KEY_PROGRESS to 1))

        return try {
            coroutineScope {
                // Start downloading timings in parallel
                val timingsDeferred = async {
                    timingsRemoteDataSource.getVerseTimings(reciterId, surahId)
                }

                val file = getLocalFile(reciterId, surahId)
                if (file.exists()) {
                    file.delete()
                }
                file.parentFile?.mkdirs()

                val connectionTime = measureTimeMillis {
                    httpClient.prepareGet(url) {
                        timeout {
                            requestTimeoutMillis = Long.MAX_VALUE
                            connectTimeoutMillis = 60_000
                            socketTimeoutMillis = Long.MAX_VALUE
                        }
                    }.execute { response ->
                        val contentLength = response.contentLength() ?: -1L

                        val channel = response.bodyAsChannel()
                        file.outputStream().buffered().use { output ->
                            val buffer = ByteArray(64 * 1024)
                            var bytesRead = 0L
                            var lastProgress = 1

                            while (!channel.isClosedForRead) {
                                val read = channel.readAvailable(buffer)
                                if (read <= 0) break
                                output.write(buffer, 0, read)
                                bytesRead += read

                                if (contentLength > 0) {
                                    val progress =
                                        (bytesRead * 100 / contentLength).toInt().coerceIn(1, 99)
                                    if (progress != lastProgress) {
                                        lastProgress = progress
                                        setProgress(workDataOf(KEY_PROGRESS to progress))
                                    }
                                }
                            }
                        }
                    }
                }

                // Wait for timings to finish
                val remoteTimings = timingsDeferred.await()

                val timingEntities = remoteTimings.map {
                    VerseTimingEntity(
                        readerId = reciterId,
                        surahId = surahId,
                        verseNumber = it.verseNumber,
                        startTimeMs = it.startTimeMs,
                        endTimeMs = it.endTimeMs
                    )
                }

                val entity = DownloadedReciterEntity(
                    id = reciterId,
                    surahId = surahId,
                    nameAr = nameAr,
                    nameEn = nameEn,
                    rewayaName = rewayaName,
                    baseAudioUrl = baseAudioUrl,
                    localFilePath = file.absolutePath
                )
                downloadedReciterDao.saveDownloadedReciterWithTimings(
                    reciter = entity,
                    timings = timingEntities
                )
            }

            Result.success()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            val file = getLocalFile(reciterId, surahId)
            if (file.exists()) {
                file.delete()
            }
            if (runAttemptCount < 3) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }

    private fun getLocalFile(reciterId: Int, surahId: Int): File {
        val folder = File(applicationContext.filesDir, "audio/reciters/$reciterId")
        return File(folder, "${surahId.toString().padStart(3, '0')}.mp3")
    }

    companion object {
        const val KEY_RECITER_ID = "reciter_id"
        const val KEY_SURAH_ID = "surah_id"
        const val KEY_URL = "url"
        const val KEY_NAME_AR = "name_ar"
        const val KEY_NAME_EN = "name_en"
        const val KEY_REWAYA_NAME = "rewaya_name"
        const val KEY_BASE_AUDIO_URL = "base_audio_url"
        const val KEY_PROGRESS = "progress"

        fun createInputData(
            reciterId: Int,
            surahId: Int,
            url: String,
            nameAr: String,
            nameEn: String,
            rewayaName: String,
            baseAudioUrl: String
        ) = workDataOf(
            KEY_RECITER_ID to reciterId,
            KEY_SURAH_ID to surahId,
            KEY_URL to url,
            KEY_NAME_AR to nameAr,
            KEY_NAME_EN to nameEn,
            KEY_REWAYA_NAME to rewayaName,
            KEY_BASE_AUDIO_URL to baseAudioUrl
        )
    }
}
