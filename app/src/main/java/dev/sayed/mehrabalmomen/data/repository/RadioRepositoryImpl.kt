@file:OptIn(ExperimentalTime::class)

package dev.sayed.mehrabalmomen.data.repository

import dev.sayed.mehrabalmomen.data.mappers.toDomain
import dev.sayed.mehrabalmomen.data.mappers.toDomainList
import dev.sayed.mehrabalmomen.data.remote.dto.RadioChannelDto
import dev.sayed.mehrabalmomen.domain.entity.RadioChannel
import dev.sayed.mehrabalmomen.domain.repository.RadioRepository
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
    }

    override suspend fun getChannelById(id: Int): RadioChannel? {
        val dto: RadioChannelDto? = supabaseClient
            .from("radio_channels")
            .select {
                filter {
                    eq("id", id)
                }
            }
            .decodeSingleOrNull<RadioChannelDto>()

        return dto?.toDomain()
    }
}


