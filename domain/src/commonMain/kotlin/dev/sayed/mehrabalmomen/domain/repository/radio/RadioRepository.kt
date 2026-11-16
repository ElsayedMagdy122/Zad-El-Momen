package dev.sayed.mehrabalmomen.domain.repository.radio

import dev.sayed.mehrabalmomen.domain.entity.radio.Category
import dev.sayed.mehrabalmomen.domain.entity.radio.RadioChannel
import kotlinx.coroutines.flow.Flow

interface RadioRepository {
    fun getAllChannels(): Flow<List<RadioChannel>>
    suspend fun getChannelById(id: Int): RadioChannel?
    suspend fun getChannelsByCategory(categoryId: String): Flow<List<RadioChannel>>
   suspend fun getCategories(): Flow<List<Category>>
}