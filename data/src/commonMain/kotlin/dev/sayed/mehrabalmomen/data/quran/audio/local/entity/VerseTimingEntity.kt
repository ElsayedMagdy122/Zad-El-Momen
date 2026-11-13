package dev.sayed.mehrabalmomen.data.quran.audio.local.entity

import androidx.room.Entity

@Entity(
    tableName = "verse_timings",
    primaryKeys = ["readerId", "surahId", "verseNumber"]
)
data class VerseTimingEntity(
    val readerId: Int,
    val surahId: Int,
    val verseNumber: Int,
    val startTimeMs: Long,
    val endTimeMs: Long
)
