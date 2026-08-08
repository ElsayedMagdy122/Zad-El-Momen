package dev.sayed.mehrabalmomen.data.quran.audio.remote

import dev.sayed.mehrabalmomen.data.quran.audio.remote.dto.QuranAudioReaderDto
import dev.sayed.mehrabalmomen.data.quran.audio.remote.dto.QuranAudioRewayatDto

interface QuranAudioReadersRemoteDataSource {
    suspend fun getReaders(): List<QuranAudioReaderDto>
    suspend fun getReaderById(readerId: Int): QuranAudioReaderDto?
    suspend fun getRewayat(): List<QuranAudioRewayatDto>
}