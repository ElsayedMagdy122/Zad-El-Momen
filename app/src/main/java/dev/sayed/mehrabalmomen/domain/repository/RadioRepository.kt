package dev.sayed.mehrabalmomen.domain.repository

import dev.sayed.mehrabalmomen.domain.entity.RadioChannel
import kotlinx.coroutines.flow.Flow

interface RadioRepository {
    fun getAllChannels(): Flow<List<RadioChannel>>
    suspend fun getChannelById(id: Int): RadioChannel?
}