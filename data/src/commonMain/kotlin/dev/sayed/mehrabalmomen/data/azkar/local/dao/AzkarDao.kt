package dev.sayed.mehrabalmomen.data.azkar.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.sayed.mehrabalmomen.data.azkar.local.entity.AzkarCustomCategoryEntity
import dev.sayed.mehrabalmomen.data.azkar.local.entity.AzkarCustomItemEntity
import dev.sayed.mehrabalmomen.data.azkar.local.entity.AzkarHistoryEntity
import dev.sayed.mehrabalmomen.data.azkar.local.entity.AzkarStatsEntity

@Dao
interface AzkarDao {

    @Query("SELECT title FROM azkar_custom_categories")
    suspend fun getCustomCategories(): List<String>

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertCategory(category: AzkarCustomCategoryEntity)

    @Query("SELECT * FROM azkar_custom_items")
    suspend fun getAllCustomItems(): List<AzkarCustomItemEntity>

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertCustomItem(item: AzkarCustomItemEntity)

    @Query("SELECT * FROM azkar_stats WHERE id = 1")
    suspend fun getStats(): AzkarStatsEntity?

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertStats(stats: AzkarStatsEntity)

    @Query("SELECT * FROM azkar_history")
    suspend fun getAllHistory(): List<AzkarHistoryEntity>

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertHistory(history: AzkarHistoryEntity)
}
