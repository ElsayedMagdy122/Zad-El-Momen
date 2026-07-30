package dev.sayed.mehrabalmomen.domain.entity.quran.audio

import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
data class QuranAudioVerseTiming(
    val readerId: Int,
    val surahId: Short,
    val verseNumber: Int,
    val startTimeMs: Long,
    val endTimeMs: Long,
    val createdAt: Instant
)