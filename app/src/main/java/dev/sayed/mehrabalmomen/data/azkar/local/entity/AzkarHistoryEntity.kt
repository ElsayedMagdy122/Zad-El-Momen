package dev.sayed.mehrabalmomen.data.azkar.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "azkar_history")
data class AzkarHistoryEntity(
    @PrimaryKey
    val date: String,
    val count: Int
)
