package dev.sayed.mehrabalmomen.data.quran.audio.remote


import dev.sayed.mehrabalmomen.data.quran.audio.remote.dto.QuranAudioReaderDto
import dev.sayed.mehrabalmomen.data.quran.audio.remote.dto.QuranAudioRewayatDto
import dev.sayed.mehrabalmomen.data.quran.audio.remote.dto.QuranAudioVerseTimingDto
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from

class QuranAudioRemoteDataSourceImpl(
    private val supabaseClient: SupabaseClient
) : QuranAudioRemoteDataSource {

    override suspend fun getReaders(): List<QuranAudioReaderDto> {
        return supabaseClient
            .from(READERS_TABLE)
            .select()
            .decodeList<QuranAudioReaderDto>()
    }

    override suspend fun getReaderById(readerId: Int): QuranAudioReaderDto? {
        return supabaseClient
            .from(READERS_TABLE)
            .select {
                filter {
                    eq("id", readerId)
                }
            }
            .decodeSingleOrNull<QuranAudioReaderDto>()
    }

    override suspend fun getRewayat(): List<QuranAudioRewayatDto> {
        return supabaseClient
            .from(REWAYAT_TABLE)
            .select()
            .decodeList<QuranAudioRewayatDto>()
    }

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
        const val READERS_TABLE = "quran_audio_readers"
        const val REWAYAT_TABLE = "quran_audio_rewayat"
        const val VERSE_TIMINGS_TABLE = "quran_audio_verse_timings"
    }
}