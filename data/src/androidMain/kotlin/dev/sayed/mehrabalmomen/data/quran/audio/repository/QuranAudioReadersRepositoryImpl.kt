package dev.sayed.mehrabalmomen.data.quran.audio.repository

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import dev.sayed.mehrabalmomen.data.quran.audio.local.dao.DownloadedReciterDao
import dev.sayed.mehrabalmomen.data.quran.audio.remote.QuranAudioReadersRemoteDataSource
import dev.sayed.mehrabalmomen.data.quran.audio.remote.toEntity
import dev.sayed.mehrabalmomen.data.settings.local.RecitationPreferences
import dev.sayed.mehrabalmomen.domain.entity.quran.audio.QuranAudioReader
import dev.sayed.mehrabalmomen.domain.model.audio.DownloadStatus
import dev.sayed.mehrabalmomen.domain.model.audio.ReciterPreference
import dev.sayed.mehrabalmomen.domain.repository.quran.QuranAudioReadersRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class QuranAudioReadersRepositoryImpl(
    private val readersRemoteDataSource: QuranAudioReadersRemoteDataSource,
    private val downloadedReciterDao: DownloadedReciterDao,
    private val recitationPreferences: RecitationPreferences,
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
        // Worker reference might need fix or use placeholder for now
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

    override suspend fun saveLastReciter(preference: ReciterPreference) {
        recitationPreferences.saveLastReciter(
            id = preference.id,
            nameAr = preference.nameAr,
            nameEn = preference.nameEn,
            baseAudioUrl = preference.baseAudioUrl,
            rewayaName = preference.rewayaName
        )
    }

    override fun observeLastReciter(): Flow<ReciterPreference?> {
        return recitationPreferences.lastReciter.map { pref ->
            pref?.let {
                ReciterPreference(
                    id = it.id,
                    nameAr = it.nameAr,
                    nameEn = it.nameEn,
                    baseAudioUrl = it.baseAudioUrl,
                    rewayaName = it.rewayaName
                )
            }
        }
    }
}
