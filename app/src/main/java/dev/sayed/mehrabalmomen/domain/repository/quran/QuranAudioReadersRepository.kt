package dev.sayed.mehrabalmomen.domain.repository.quran

interface QuranAudioReadersRepository {
    suspend fun getReaders(): List<QuranAudioReader>
}