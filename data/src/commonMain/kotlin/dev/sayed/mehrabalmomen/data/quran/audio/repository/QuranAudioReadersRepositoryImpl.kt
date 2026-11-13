package dev.sayed.mehrabalmomen.data.quran.audio.repository

import dev.sayed.mehrabalmomen.data.quran.audio.ReciterDownloader
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

/**
 * Implementation of [QuranAudioReadersRepository] handling data sources and platform tasks.
 */
class QuranAudioReadersRepositoryImpl(
    private val readersRemoteDataSource: QuranAudioReadersRemoteDataSource,
    private val downloadedReciterDao: DownloadedReciterDao,
    private val recitationPreferences: RecitationPreferences,
    private val reciterDownloader: ReciterDownloader
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
        reciterDownloader.downloadReciter(reciter, surahId)
    }

    override fun observeDownloadStatus(reciterId: Int, surahId: Int): Flow<DownloadStatus> {
        return reciterDownloader.observeDownloadStatus(reciterId, surahId)
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
