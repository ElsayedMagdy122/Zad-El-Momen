package dev.sayed.mehrabalmomen.domain.repository.quran

interface QuranAudioRepository {
    suspend fun getTrack(
        readerId: Int,
        surahId: Int
    ): QuranAudioTrack?

    suspend fun getVerseTimings(
        readerId: Int,
        surahId: Int
    ): List<QuranAudioVerseTiming>
}