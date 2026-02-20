package dev.sayed.mehrabalmomen.data.local.quran.dto

import androidx.room.Entity

@Entity(
    tableName = "bookmark_table",
    primaryKeys = ["surahId", "ayahId"]
)
data class BookmarkEntity(
    val surahId: Int,
    val ayahId: Int,
    val arabicName: String,
    val englishName: String,
    val text: String,
    val bookmarkedAt: Long = System.currentTimeMillis()
)