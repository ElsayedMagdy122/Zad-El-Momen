package dev.sayed.mehrabalmomen.data.quran.audio.repository

import dev.sayed.mehrabalmomen.domain.repository.quran.QuranAudioRepository

class QuranAudioRepositoryImpl(

) : QuranAudioRepository {
    override suspend fun getTrack(
        readerId: Int,
        surahId: Int
    ): QuranAudioTrack? {
        TODO("Not yet implemented")
    }

    override suspend fun getVerseTimings(
        readerId: Int,
        surahId: Int
    ): List<QuranAudioVerseTiming> {
        TODO("Not yet implemented")
    }
}