package dev.sayed.mehrabalmomen.domain.repository.azkar

import dev.sayed.mehrabalmomen.domain.entity.azkar.AzkarCategory
import dev.sayed.mehrabalmomen.domain.entity.azkar.AzkarItem
import dev.sayed.mehrabalmomen.domain.entity.azkar.AzkarStats

interface AzkarRepository {
    suspend fun getAzkarCategories(): List<AzkarCategory>
    suspend fun addCategory(title: String)
    suspend fun addDhikr(categoryTitle: String, item: AzkarItem)
    suspend fun getAzkarStats(): AzkarStats
    suspend fun recordItemCompleted(itemKey: String)
    suspend fun getCompletedItemsToday(): Set<String>
    suspend fun getAzkarHistory(): Map<String, Int>
}
