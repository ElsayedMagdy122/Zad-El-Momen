package dev.sayed.mehrabalmomen.domain.entity.quran.bookmark

import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
data class Bookmark(
    val surahId: Int,
    val ayahId: Int,
    val arabicName: String,
    val englishName: String,
    val text: String,
    val bookmarkedAt: Long = Clock.System.now().toEpochMilliseconds()
)