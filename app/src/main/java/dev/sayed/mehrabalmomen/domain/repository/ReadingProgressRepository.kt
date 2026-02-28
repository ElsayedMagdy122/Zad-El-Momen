package dev.sayed.mehrabalmomen.domain.repository

import dev.sayed.mehrabalmomen.domain.entity.quran.ReadingProgress
import kotlinx.coroutines.flow.Flow

interface ReadingProgressRepository {
    suspend fun save(surahId: Int, ayahId: Int)
    fun observe(): Flow<ReadingProgress?>
}