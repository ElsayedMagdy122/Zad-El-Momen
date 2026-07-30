package dev.sayed.mehrabalmomen.data.quran.audio.repository

import dev.sayed.mehrabalmomen.data.quran.audio.remote.QuranAudioReadersRemoteDataSource
import dev.sayed.mehrabalmomen.data.quran.audio.remote.QuranAudioTimingsRemoteDataSource
import dev.sayed.mehrabalmomen.data.quran.audio.remote.buildAudioTrack
import dev.sayed.mehrabalmomen.data.quran.audio.remote.toEntity
import dev.sayed.mehrabalmomen.domain.entity.quran.audio.QuranAudioTrack
import dev.sayed.mehrabalmomen.domain.entity.quran.audio.QuranAudioVerseTiming
import dev.sayed.mehrabalmomen.domain.repository.quran.QuranAudioRepository


class QuranAudioRepositoryImpl(
    private val readersRemoteDataSource: QuranAudioReadersRemoteDataSource,
    private val timingsRemoteDataSource: QuranAudioTimingsRemoteDataSource
) : QuranAudioRepository {

    override suspend fun getTrack(
        readerId: Int,
        surahId: Int
    ): QuranAudioTrack? {
        val readerDto = readersRemoteDataSource.getReaderById(readerId) ?: return null
        return buildAudioTrack(reader = readerDto, surahId = surahId)
    }

    override suspend fun getVerseTimings(
        readerId: Int,
        surahId: Int
    ): List<QuranAudioVerseTiming> {
        return timingsRemoteDataSource
            .getVerseTimings(readerId, surahId)
            .map { it.toEntity() }
    }
}