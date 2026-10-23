package dev.sayed.mehrabalmomen.data.azkar.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "azkar_custom_items")
data class AzkarCustomItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val categoryTitle: String,
    val content: String,
    val count: String,
    val description: String?,
    val reference: String?
)
