package dev.sayed.mehrabalmomen.domain.repository.quran

import dev.sayed.mehrabalmomen.domain.entity.quran.audio.QuranAudioTrack
import dev.sayed.mehrabalmomen.domain.entity.quran.audio.QuranAudioVerseTiming

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