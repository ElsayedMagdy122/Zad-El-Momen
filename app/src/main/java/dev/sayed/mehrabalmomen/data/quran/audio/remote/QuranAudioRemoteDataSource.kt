package dev.sayed.mehrabalmomen.data.quran.audio.remote

import dev.sayed.mehrabalmomen.data.quran.audio.remote.dto.QuranAudioReaderDto
import dev.sayed.mehrabalmomen.data.quran.audio.remote.dto.QuranAudioVerseTimingDto

interface QuranAudioRemoteDataSource {
    suspend fun getReaders(): List<QuranAudioReaderDto>
    suspend fun getReaderById(readerId: Int): QuranAudioReaderDto?
    suspend fun getVerseTimings(readerId: Int, surahId: Int): List<QuranAudioVerseTimingDto>
}