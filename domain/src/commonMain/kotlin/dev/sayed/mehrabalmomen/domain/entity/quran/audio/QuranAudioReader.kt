package dev.sayed.mehrabalmomen.domain.entity.quran.audio

import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
data class QuranAudioReader(
    val id: Int,
    val nameAr: String,
    val nameEn: String,
    val baseAudioUrl: String,
    val surahsCount: Short?,
    val rewaya: QuranAudioRewayat?
)
