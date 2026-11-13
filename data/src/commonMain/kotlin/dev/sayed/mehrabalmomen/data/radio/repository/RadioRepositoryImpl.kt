package dev.sayed.mehrabalmomen.data.radio.repository

import dev.sayed.mehrabalmomen.data.radio.dto.CategoryDto
import dev.sayed.mehrabalmomen.data.radio.dto.RadioChannelDto
import dev.sayed.mehrabalmomen.data.radio.mapper.toDomain
import dev.sayed.mehrabalmomen.data.radio.mapper.toDomainList
import dev.sayed.mehrabalmomen.data.util.helpers.safeCall
import dev.sayed.mehrabalmomen.data.util.helpers.safeFlow
import dev.sayed.mehrabalmomen.domain.entity.radio.Category
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
            .from(RADIO_CHANNELS_TABLE)
            .select()
            .decodeList<RadioChannelDto>()
        emit(channelsDto.toDomainList())
    }.safeFlow()

    override suspend fun getChannelById(id: Int) =
        safeCall {
            val dto: RadioChannelDto? = supabaseClient
                .from(RADIO_CHANNELS_TABLE)
                .select {
                    filter {
                        eq("id", id)
                    }
                }
                .decodeSingleOrNull<RadioChannelDto>()
            dto?.toDomain()
        }
    override suspend fun getChannelsByCategory(categoryId: String): Flow<List<RadioChannel>> = flow {
        val channelsDto: List<RadioChannelDto> = supabaseClient
            .from(RADIO_CHANNELS_TABLE)
            .select {
                filter {
                    eq("category_id", categoryId)
                }
            }
            .decodeList<RadioChannelDto>()

        emit(channelsDto.toDomainList())
    }.safeFlow()

    override suspend fun getCategories(): Flow<List<Category>> = flow {
        val result = supabaseClient
            .from(CATEGORIES_TABLE)
            .select()
            .decodeList<CategoryDto>()

        emit(result.map { it.toDomain() })
    }.safeFlow()

    private companion object{
        const val RADIO_CHANNELS_TABLE = "radio_channels"
        const val CATEGORIES_TABLE = "categories"
    }
}