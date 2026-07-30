package dev.sayed.mehrabalmomen.domain.entity.quran.tilawah

data class QuranAudioVerseTiming(
    val readerId: Int,
    val surahId: Int,
    val verseNumber: Int,
    val startTimeMs: Long,
    val endTimeMs: Long
)