package dev.sayed.mehrabalmomen.data.quran.audio.remote

import dev.sayed.mehrabalmomen.data.quran.audio.remote.dto.QuranAudioReaderDto
import dev.sayed.mehrabalmomen.data.quran.audio.remote.dto.QuranAudioRewayatDto
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from

class QuranAudioReadersRemoteDataSourceImpl(
    private val supabaseClient: SupabaseClient
) : QuranAudioReadersRemoteDataSource {

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

    private companion object {
        const val READERS_TABLE = "quran_audio_readers"
        const val REWAYAT_TABLE = "quran_audio_rewayat"
    }
}