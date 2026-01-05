package dev.sayed.mehrabalmomen.domain.repository

import dev.sayed.mehrabalmomen.domain.entity.AzkarCategory

interface AzkarRepository {
    suspend fun getAzkarCategories(): List<AzkarCategory>
}