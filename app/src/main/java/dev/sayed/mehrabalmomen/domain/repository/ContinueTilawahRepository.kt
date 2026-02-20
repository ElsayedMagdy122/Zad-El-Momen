package dev.sayed.mehrabalmomen.domain.repository

import dev.sayed.mehrabalmomen.domain.model.ContinueTilawah
import kotlinx.coroutines.flow.Flow

interface ContinueTilawahRepository {
    suspend fun save(surahId: Int, ayahId: Int)
    fun observe(): Flow<ContinueTilawah?>
}