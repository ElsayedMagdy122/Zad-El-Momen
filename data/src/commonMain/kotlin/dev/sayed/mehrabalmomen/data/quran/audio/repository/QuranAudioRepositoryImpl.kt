package dev.sayed.mehrabalmomen.data.quran.audio.repository

import dev.sayed.mehrabalmomen.data.quran.audio.local.dao.DownloadedReciterDao
import dev.sayed.mehrabalmomen.data.quran.audio.remote.QuranAudioReadersRemoteDataSource
import dev.sayed.mehrabalmomen.data.quran.audio.remote.QuranAudioTimingsRemoteDataSource
import dev.sayed.mehrabalmomen.data.quran.audio.remote.buildAudioTrack
import dev.sayed.mehrabalmomen.data.quran.audio.remote.toEntity
import dev.sayed.mehrabalmomen.domain.entity.quran.audio.QuranAudioTrack
import dev.sayed.mehrabalmomen.domain.entity.quran.audio.QuranAudioVerseTiming
import dev.sayed.mehrabalmomen.domain.repository.quran.QuranAudioRepository

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
        if (downloaded != null) {
            val localPath = downloaded.localFilePath
            if (localPath.isNotEmpty()) {
                return QuranAudioTrack(
                    id = "${readerId}${surahId.toString().padStart(3, '0')}".toLongOrNull() ?: 0L,
                    readerId = readerId,
                    surahId = surahId,
                    audioUrl = if (localPath.startsWith("file://")) localPath else "file://$localPath"
                )
            }
        }

        val readerDto = readersRemoteDataSource.getReaderById(readerId) ?: return null
        return buildAudioTrack(reader = readerDto, surahId = surahId)
    }

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
