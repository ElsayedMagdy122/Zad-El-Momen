package dev.sayed.mehrabalmomen.data.quran.audio.remote

import dev.sayed.mehrabalmomen.data.quran.audio.remote.dto.QuranAudioVerseTimingDto
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from

class QuranAudioTimingsRemoteDataSourceImpl(
    private val supabaseClient: SupabaseClient
) : QuranAudioTimingsRemoteDataSource {

    override suspend fun getVerseTimings(
        readerId: Int,
        surahId: Int
    ): List<QuranAudioVerseTimingDto> {
        return supabaseClient
            .from(VERSE_TIMINGS_TABLE)
            .select {
                filter {
                    eq("reader_id", readerId)
                    eq("surah_id", surahId)
                }
            }
            .decodeList<QuranAudioVerseTimingDto>()
    }

    private companion object {
        const val VERSE_TIMINGS_TABLE = "quran_audio_verse_timings"
    }
}