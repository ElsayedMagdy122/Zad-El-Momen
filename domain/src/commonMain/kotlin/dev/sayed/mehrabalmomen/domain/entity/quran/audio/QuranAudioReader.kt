package dev.sayed.mehrabalmomen.domain.entity.quran.audio

import kotlin.time.Instant

data class QuranAudioReader(
    val id: Int,
    val nameAr: String,
    val nameEn: String,
    val baseAudioUrl: String,
    val surahsCount: Short?,
    val rewaya: QuranAudioRewayat?,
    val createdAt: Instant? = null
)