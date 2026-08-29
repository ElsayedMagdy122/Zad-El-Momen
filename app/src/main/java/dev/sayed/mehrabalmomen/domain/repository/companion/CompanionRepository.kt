package dev.sayed.mehrabalmomen.domain.repository.companion

import dev.sayed.mehrabalmomen.domain.model.companion.CompanionState
import kotlinx.coroutines.flow.Flow

interface CompanionRepository {
    fun observeCompanionState(): Flow<CompanionState>
    fun observeCompanionEnabled(): Flow<Boolean>
    suspend fun updateQuranReadStatus(read: Boolean)
    suspend fun updateAzkarReadStatus(read: Boolean)
    suspend fun updateLastInteraction(millis: Long)
    suspend fun updateCompanionEnabled(enabled: Boolean)
}
