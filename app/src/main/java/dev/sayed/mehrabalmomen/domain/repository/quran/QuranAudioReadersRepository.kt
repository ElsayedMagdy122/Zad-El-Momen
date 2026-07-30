package dev.sayed.mehrabalmomen.domain.repository.quran

import dev.sayed.mehrabalmomen.domain.entity.quran.tilawah.QuranAudioReader

interface QuranAudioReadersRepository {
    suspend fun getReaders(): List<QuranAudioReader>
}