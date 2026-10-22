package dev.sayed.mehrabalmomen.data.azkar.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "azkar_custom_categories")
data class AzkarCustomCategoryEntity(
    @PrimaryKey
    val title: String
)
