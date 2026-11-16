package dev.sayed.mehrabalmomen.domain.entity.quran.audio

import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
data class QuranAudioSurah(
    val id: Short,
    val nameAr: String,
    val nameEn: String,
    val versesCount: Short,
    val createdAt: Instant?
)
