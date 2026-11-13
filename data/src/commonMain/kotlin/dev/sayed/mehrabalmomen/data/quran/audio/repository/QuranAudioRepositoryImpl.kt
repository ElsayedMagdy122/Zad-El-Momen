package dev.sayed.mehrabalmomen.data.quran.audio.repository

import dev.sayed.mehrabalmomen.data.quran.audio.local.dao.DownloadedReciterDao
import dev.sayed.mehrabalmomen.data.quran.audio.remote.QuranAudioReadersRemoteDataSource
import dev.sayed.mehrabalmomen.data.quran.audio.remote.QuranAudioTimingsRemoteDataSource
import dev.sayed.mehrabalmomen.data.quran.audio.remote.buildAudioTrack
import dev.sayed.mehrabalmomen.data.quran.audio.remote.toEntity
import dev.sayed.mehrabalmomen.domain.entity.quran.audio.QuranAudioTrack
import dev.sayed.mehrabalmomen.domain.entity.quran.audio.QuranAudioVerseTiming
import dev.sayed.mehrabalmomen.domain.repository.quran.QuranAudioRepository
import kotlin.time.ExperimentalTime

class QuranAudioRepositoryImpl(
    private val readersRemoteDataSource: QuranAudioReadersRemoteDataSource,
    private val timingsRemoteDataSource: QuranAudioTimingsRemoteDataSource,
    private val downloadedReciterDao: DownloadedReciterDao
) : QuranAudioRepository {

    override suspend fun getTrack(
        readerId: Int,
        surahId: Int
    ): QuranAudioTrack? {
        val downloaded = downloadedReciterDao.getDownloadedReciter(readerId, surahId)
        if (downloaded != null && downloaded.localFilePath.isNotBlank()) {
            val audioUrl = if (downloaded.localFilePath.startsWith("file://")) {
                downloaded.localFilePath
            } else {
                "file://${downloaded.localFilePath}"
            }
            return QuranAudioTrack(
                id = "${readerId}${surahId.toString().padStart(3, '0')}".toLongOrNull() ?: 0L,
                readerId = readerId,
                surahId = surahId,
                audioUrl = audioUrl
            )
        }

        val readerDto = readersRemoteDataSource.getReaderById(readerId) ?: return null
        return buildAudioTrack(reader = readerDto, surahId = surahId)
    }

    @OptIn(ExperimentalTime::class)
    override suspend fun getVerseTimings(
        readerId: Int,
        surahId: Int
    ): List<QuranAudioVerseTiming> {
        val localTimings = downloadedReciterDao.getVerseTimings(readerId, surahId)
        if (localTimings.isNotEmpty()) {
            return localTimings.map {
                QuranAudioVerseTiming(
                    readerId = it.readerId,
                    surahId = it.surahId.toShort(),
                    verseNumber = it.verseNumber,
                    startTimeMs = it.startTimeMs,
                    endTimeMs = it.endTimeMs,
                    createdAt = null
                )
            }
        }

        return timingsRemoteDataSource
            .getVerseTimings(readerId, surahId)
            .map { it.toEntity() }
    }
}
