package dev.sayed.mehrabalmomen.domain.repository.azkar

import dev.sayed.mehrabalmomen.domain.entity.azkar.AzkarCategory

interface AzkarRepository {
    suspend fun getAzkarCategories(): List<AzkarCategory>
}