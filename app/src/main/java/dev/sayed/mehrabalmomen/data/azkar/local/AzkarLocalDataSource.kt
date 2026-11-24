package dev.sayed.mehrabalmomen.data.azkar.local

import android.content.Context
import dev.sayed.mehrabalmomen.data.azkar.local.dao.AzkarDao
import dev.sayed.mehrabalmomen.data.azkar.local.dto.AzkarItemDto
import dev.sayed.mehrabalmomen.data.azkar.local.entity.AzkarCustomCategoryEntity
import dev.sayed.mehrabalmomen.data.azkar.local.entity.AzkarCustomItemEntity
import dev.sayed.mehrabalmomen.data.azkar.local.entity.AzkarHistoryEntity
import dev.sayed.mehrabalmomen.data.azkar.local.entity.AzkarStatsEntity
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class AzkarLocalDataSource(
    private val context: Context,
    private val azkarDao: AzkarDao,
    private val json: Json
) {
    private val flexibleJson = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    fun getAzkar(): Map<String, List<AzkarItemDto>> {
        return try {
            val jsonString = context.assets
                .open("azkar.json")
                .bufferedReader()
                .use { it.readText() }

            flexibleJson.decodeFromString<Map<String, List<AzkarItemDto>>>(jsonString)
        } catch (e: Exception) {
            emptyMap()
        }
    }

    suspend fun getCustomCategories(): List<String> {
        return azkarDao.getCustomCategories()
    }

    suspend fun saveCustomCategory(title: String) {
        if (title.isBlank()) return
        azkarDao.insertCategory(AzkarCustomCategoryEntity(title = title))
    }

    suspend fun getCustomItems(): Map<String, List<AzkarItemDto>> {
        val entities = azkarDao.getAllCustomItems()
        val map = mutableMapOf<String, MutableList<AzkarItemDto>>()
        for (entity in entities) {
            val list = map.getOrPut(entity.categoryTitle) { mutableListOf() }
            list.add(
                AzkarItemDto(
                    content = entity.content,
                    count = entity.count,
                    description = entity.description,
                    reference = entity.reference
                )
            )
        }
        return map
    }

    suspend fun saveCustomItem(categoryTitle: String, item: AzkarItemDto) {
        azkarDao.insertCustomItem(
            AzkarCustomItemEntity(
                categoryTitle = categoryTitle,
                content = item.content,
                count = item.count,
                description = item.description,
                reference = item.reference
            )
        )
    }

    suspend fun getStreak(): Int {
        val stats = azkarDao.getStats()
        return stats?.streak ?: 0
    }

    suspend fun setStreak(streak: Int) {
        val stats = azkarDao.getStats()
        val today = java.time.LocalDate.now().toString()
        azkarDao.insertStats(
            AzkarStatsEntity(
                streak = streak,
                lastDate = stats?.lastDate ?: today,
                completedTodayJson = stats?.completedTodayJson ?: "[]"
            )
        )
    }

    suspend fun getLastDate(): String {
        val stats = azkarDao.getStats()
        return stats?.lastDate ?: ""
    }

    suspend fun setLastDate(date: String) {
        val stats = azkarDao.getStats()
        azkarDao.insertStats(
            AzkarStatsEntity(
                streak = stats?.streak ?: 0,
                lastDate = date,
                completedTodayJson = stats?.completedTodayJson ?: "[]"
            )
        )
    }

    suspend fun getCompletedTodaySet(): Set<String> {
        val stats = azkarDao.getStats() ?: return emptySet()
        val lastDate = stats.lastDate
        val today = java.time.LocalDate.now().toString()
        if (lastDate == today && stats.completedTodayJson.isNotEmpty()) {
            return try {
                flexibleJson.decodeFromString(stats.completedTodayJson)
            } catch (_: Exception) {
                emptySet()
            }
        }
        return emptySet()
    }

    suspend fun saveCompletedTodaySet(set: Set<String>) {
        val stats = azkarDao.getStats()
        val today = java.time.LocalDate.now().toString()
        azkarDao.insertStats(
            AzkarStatsEntity(
                streak = stats?.streak ?: 0,
                lastDate = stats?.lastDate ?: today,
                completedTodayJson = flexibleJson.encodeToString(set)
            )
        )
    }

    suspend fun getHistoryMap(): Map<String, Int> {
        val entities = azkarDao.getAllHistory()
        return entities.associate { it.date to it.count }
    }

    suspend fun saveHistoryMap(historyMap: Map<String, Int>) {
        for ((date, count) in historyMap) {
            azkarDao.insertHistory(AzkarHistoryEntity(date = date, count = count))
        }
    }
}
