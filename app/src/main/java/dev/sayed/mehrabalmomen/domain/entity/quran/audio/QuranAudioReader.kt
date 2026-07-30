package dev.sayed.mehrabalmomen.domain.entity.quran.audio

import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
data class QuranAudioReader(
    val id: Int,
    val nameAr: String,
    val nameEn: String,
    val baseAudioUrl: String,
    val surahsCount: Short,
    val rewayaId: Int,
    val rewaya: QuranAudioRewayat,
    val createdAt: Instant
)