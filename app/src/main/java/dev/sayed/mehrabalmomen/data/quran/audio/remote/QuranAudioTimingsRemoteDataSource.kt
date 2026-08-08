package dev.sayed.mehrabalmomen.data.quran.audio.remote

import dev.sayed.mehrabalmomen.data.quran.audio.remote.dto.QuranAudioVerseTimingDto

interface QuranAudioTimingsRemoteDataSource {
    suspend fun getVerseTimings(readerId: Int, surahId: Int): List<QuranAudioVerseTimingDto>
}