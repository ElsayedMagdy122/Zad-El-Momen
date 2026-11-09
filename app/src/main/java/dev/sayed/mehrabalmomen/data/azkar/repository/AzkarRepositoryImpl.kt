package dev.sayed.mehrabalmomen.data.azkar.repository

import dev.sayed.mehrabalmomen.data.azkar.local.AzkarLocalDataSource
import dev.sayed.mehrabalmomen.data.azkar.local.dto.AzkarCategoryDto
import dev.sayed.mehrabalmomen.data.azkar.local.dto.AzkarItemDto
import dev.sayed.mehrabalmomen.data.azkar.mapper.toDomain
import dev.sayed.mehrabalmomen.domain.entity.azkar.AzkarCategory
import dev.sayed.mehrabalmomen.domain.entity.azkar.AzkarItem
import dev.sayed.mehrabalmomen.domain.entity.azkar.AzkarStats
import dev.sayed.mehrabalmomen.domain.repository.azkar.AzkarRepository

class AzkarRepositoryImpl(
    private val localDataSource: AzkarLocalDataSource
) : AzkarRepository {

    override suspend fun getAzkarCategories(): List<AzkarCategory> {
        val rawData = localDataSource.getAzkar()
        val defaultCategories = rawData.map { (title, items) ->
            AzkarCategoryDto(title = title, items = items).toDomain()
        }.toMutableList()

        val customCategoryTitles = localDataSource.getCustomCategories()
        val customItemsMap = localDataSource.getCustomItems()

        for (title in customCategoryTitles) {
            if (defaultCategories.none { it.title == title }) {
                val items = customItemsMap[title]?.map { it.toDomain() } ?: emptyList()
                defaultCategories.add(AzkarCategory(title = title, items = items))
            }
        }

        for (i in defaultCategories.indices) {
            val cat = defaultCategories[i]
            val extraItems = customItemsMap[cat.title]?.map { it.toDomain() } ?: emptyList()
            if (extraItems.isNotEmpty()) {
                val combinedItems = (cat.items + extraItems).distinctBy { it.content }
                defaultCategories[i] = cat.copy(items = combinedItems)
            }
        }

        return defaultCategories
    }

    override suspend fun addCategory(title: String) {
        if (title.isBlank()) return
        val currentList = localDataSource.getCustomCategories()
        if (!currentList.contains(title)) {
            localDataSource.saveCustomCategory(title)
        }
    }

    override suspend fun addDhikr(categoryTitle: String, item: AzkarItem) {
        if (categoryTitle.isBlank() || item.content.isBlank()) return
        val currentMap = localDataSource.getCustomItems()
        val currentList = currentMap[categoryTitle] ?: emptyList()
        if (currentList.none { it.content == item.content }) {
            val newItemDto = AzkarItemDto(
                content = item.content,
                count = item.count,
                description = item.description.ifEmpty { null },
                reference = item.reference.ifEmpty { null }
            )
            localDataSource.saveCustomItem(categoryTitle, newItemDto)
        }
    }

    override suspend fun getAzkarStats(): AzkarStats {
        val categories = getAzkarCategories()
        val totalAzkarCount = categories.sumOf { it.items.size }

        val streak = localDataSource.getStreak()
        val lastDate = localDataSource.getLastDate()
        val completedSet = localDataSource.getCompletedTodaySet()

        val today = java.time.LocalDate.now().toString()

        val actualStreak = if (lastDate.isNotEmpty()) {
            try {
                val last = java.time.LocalDate.parse(lastDate)
                val now = java.time.LocalDate.parse(today)
                val diff = java.time.temporal.ChronoUnit.DAYS.between(last, now)
                if (diff > 1 && completedSet.isEmpty()) 0 else streak
            } catch (_: Exception) {
                streak
            }
        } else {
            streak
        }

        val completedCountToday = completedSet.size.coerceAtMost(totalAzkarCount)
        val remainingCount = (totalAzkarCount - completedCountToday).coerceAtLeast(0)
        val completionPercentage = if (totalAzkarCount > 0) (completedCountToday * 100) / totalAzkarCount else 0

        return AzkarStats(
            streak = actualStreak.coerceAtLeast(if (completedCountToday > 0) 1 else 0),
            completedCountToday = completedCountToday,
            totalAzkarCount = totalAzkarCount,
            remainingCount = remainingCount,
            completionPercentage = completionPercentage
        )
    }

    override suspend fun recordItemCompleted(itemKey: String) {
        if (itemKey.isBlank()) return
        val today = java.time.LocalDate.now().toString()
        val lastDate = localDataSource.getLastDate()
        val currentSet = localDataSource.getCompletedTodaySet()

        if (!currentSet.contains(itemKey)) {
            val newSet = currentSet + itemKey
            localDataSource.saveCompletedTodaySet(newSet)
            localDataSource.setLastDate(today)

            val currentHistory = localDataSource.getHistoryMap()
            val newHistory = currentHistory.toMutableMap()
            newHistory[today] = newSet.size
            localDataSource.saveHistoryMap(newHistory)

            val currentStreak = localDataSource.getStreak()
            if (lastDate != today) {
                val newStreak = if (lastDate.isNotEmpty()) {
                    try {
                        val last = java.time.LocalDate.parse(lastDate)
                        val now = java.time.LocalDate.parse(today)
                        val diff = java.time.temporal.ChronoUnit.DAYS.between(last, now)
                        if (diff == 1L) currentStreak + 1 else if (diff == 0L) currentStreak else 1
                    } catch (_: Exception) {
                        1
                    }
                } else {
                    1
                }
                localDataSource.setStreak(newStreak)
            } else if (currentStreak == 0) {
                localDataSource.setStreak(1)
            }
        }
    }

    override suspend fun getCompletedItemsToday(): Set<String> {
        return localDataSource.getCompletedTodaySet()
    }

    override suspend fun getAzkarHistory(): Map<String, Int> {
        return localDataSource.getHistoryMap()
    }
}
