package dev.sayed.mehrabalmomen.data.quran.audio.repository

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import dev.sayed.mehrabalmomen.data.quran.audio.local.dao.DownloadedReciterDao
import dev.sayed.mehrabalmomen.data.quran.audio.remote.QuranAudioReadersRemoteDataSource
import dev.sayed.mehrabalmomen.data.quran.audio.remote.toEntity
import dev.sayed.mehrabalmomen.data.quran.audio.worker.DownloadReciterWorker
import dev.sayed.mehrabalmomen.domain.entity.quran.audio.QuranAudioReader
import dev.sayed.mehrabalmomen.domain.repository.quran.QuranAudioReadersRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class QuranAudioReadersRepositoryImpl(
    private val readersRemoteDataSource: QuranAudioReadersRemoteDataSource,
    private val downloadedReciterDao: DownloadedReciterDao,
    private val context: Context
) : QuranAudioReadersRepository {

    override suspend fun getReaders(): List<QuranAudioReader> {
        val readersDto = readersRemoteDataSource.getReaders()
        val rewayatDto = readersRemoteDataSource.getRewayat()

        val rewayatMap = rewayatDto
            .map { it.toEntity() }
            .associateBy { it.id }

        return readersDto.map { readerDto ->
            val rewayaEntity = readerDto.rewayaId?.let { rewayatMap[it] }
            readerDto.toEntity(rewaya = rewayaEntity)
        }
    }

    override fun getDownloadedReciters(surahId: Int): Flow<List<QuranAudioReader>> {
        return downloadedReciterDao.getDownloadedRecitersForSurah(surahId).map { entities ->
            entities.map { it.toEntity() }
        }
    }

    override suspend fun getDownloadedRecitersOnce(): List<QuranAudioReader> {
        return downloadedReciterDao.getAllDownloadedRecitersOnce().map { it.toEntity() }
    }

    override suspend fun isReciterDownloaded(reciterId: Int, surahId: Int): Boolean {
        return downloadedReciterDao.getDownloadedReciter(reciterId, surahId) != null
    }

    override suspend fun getDownloadedReciter(reciterId: Int, surahId: Int): QuranAudioReader? {
        return downloadedReciterDao.getDownloadedReciter(reciterId, surahId)?.toEntity()
    }

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

    override fun getDownloadWorkInfo(
        reciterId: Int,
        surahId: Int
    ): Flow<List<androidx.work.WorkInfo>> {
        return WorkManager.getInstance(context)
            .getWorkInfosForUniqueWorkFlow("download_reciter_${reciterId}_$surahId")
    }
}
