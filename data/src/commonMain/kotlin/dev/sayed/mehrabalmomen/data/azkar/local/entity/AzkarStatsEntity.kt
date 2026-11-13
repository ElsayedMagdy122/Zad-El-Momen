package dev.sayed.mehrabalmomen.data.azkar.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "azkar_stats")
data class AzkarStatsEntity(
    @PrimaryKey
    val id: Int = 1,
    val streak: Int,
    val lastDate: String,
    val completedTodayJson: String
)
