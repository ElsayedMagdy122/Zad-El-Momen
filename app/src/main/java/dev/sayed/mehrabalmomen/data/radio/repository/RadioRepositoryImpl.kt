package dev.sayed.mehrabalmomen.data.radio.repository

import dev.sayed.mehrabalmomen.data.radio.dto.RadioChannelDto
import dev.sayed.mehrabalmomen.data.radio.mapper.toDomain
import dev.sayed.mehrabalmomen.data.radio.mapper.toDomainList
import dev.sayed.mehrabalmomen.data.util.helpers.safeCall
import dev.sayed.mehrabalmomen.data.util.helpers.safeFlow
import dev.sayed.mehrabalmomen.domain.entity.radio.RadioChannel
import dev.sayed.mehrabalmomen.domain.repository.radio.RadioRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class RadioRepositoryImpl(
    private val supabaseClient: SupabaseClient
) : RadioRepository {

    override fun getAllChannels(): Flow<List<RadioChannel>> = flow {
        val channelsDto: List<RadioChannelDto> = supabaseClient
            .from("radio_channels")
            .select()
            .decodeList<RadioChannelDto>()
        emit(channelsDto.toDomainList())
    }.safeFlow()

    override suspend fun getChannelById(id: Int) =
        safeCall {
            val dto: RadioChannelDto? = supabaseClient
                .from("radio_channels")
                .select {
                    filter {
                        eq("id", id)
                    }
                }
                .decodeSingleOrNull<RadioChannelDto>()
            dto?.toDomain()
        }
}