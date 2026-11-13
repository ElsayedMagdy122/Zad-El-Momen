package dev.sayed.mehrabalmomen.data.quran.audio

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import dev.sayed.mehrabalmomen.data.quran.audio.worker.DownloadReciterWorker
import dev.sayed.mehrabalmomen.domain.entity.quran.audio.QuranAudioReader
import dev.sayed.mehrabalmomen.domain.model.audio.DownloadStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AndroidReciterDownloader(
    private val context: Context
) : ReciterDownloader {

    override fun downloadReciter(reciter: QuranAudioReader, surahId: Int) {
        val formattedSurahId = surahId.toString().padStart(3, '0')
        val cleanBaseUrl = reciter.baseAudioUrl.trimEnd('/')
        val fullAudioUrl = "$cleanBaseUrl/$formattedSurahId.mp3"

        val inputData = DownloadReciterWorker.createInputData(
            reciterId = reciter.id,
            surahId = surahId,
            url = fullAudioUrl,
            nameAr = reciter.nameAr,
            nameEn = reciter.nameEn,
            rewayaName = reciter.rewaya?.nameAr ?: "",
            baseAudioUrl = reciter.baseAudioUrl
        )

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<DownloadReciterWorker>()
            .setInputData(inputData)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "download_reciter_${reciter.id}_$surahId",
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }

    override fun observeDownloadStatus(reciterId: Int, surahId: Int): Flow<DownloadStatus> {
        return WorkManager.getInstance(context)
            .getWorkInfosForUniqueWorkFlow("download_reciter_${reciterId}_$surahId")
            .map { workInfos ->
                val workInfo = workInfos.firstOrNull()
                val progress = workInfo?.progress?.getInt("progress", 0) ?: 0
                val state = when (workInfo?.state) {
                    WorkInfo.State.RUNNING -> DownloadStatus.State.DOWNLOADING
                    WorkInfo.State.SUCCEEDED -> DownloadStatus.State.COMPLETED
                    WorkInfo.State.FAILED, WorkInfo.State.CANCELLED -> DownloadStatus.State.FAILED
                    else -> DownloadStatus.State.IDLE
                }
                DownloadStatus(progress, state)
            }
    }
}
