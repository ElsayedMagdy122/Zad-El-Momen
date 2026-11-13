package dev.sayed.mehrabalmomen.data.quran.text.local.dto

import androidx.room.Entity
import kotlin.time.Clock

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
    val bookmarkedAt: Long = Clock.System.now().toEpochMilliseconds()
)