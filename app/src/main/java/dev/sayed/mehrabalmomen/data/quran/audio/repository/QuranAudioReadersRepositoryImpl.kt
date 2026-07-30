package dev.sayed.mehrabalmomen.data.quran.audio.repository

import dev.sayed.mehrabalmomen.domain.repository.quran.QuranAudioReadersRepository

class QuranAudioReadersRepositoryImpl : QuranAudioReadersRepository {
    override suspend fun getReaders(): List<QuranAudioReader> {
        TODO("Not yet implemented")
    }
}