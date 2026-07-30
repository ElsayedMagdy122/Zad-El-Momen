package dev.sayed.mehrabalmomen.domain.entity.quran.tilawah

data class QuranAudioReader(
    val id: Int,
    val nameAr: String,
    val nameEn: String,
    val rewayaAr: String?,
    val rewayaEn: String?,
    val baseAudioUrl: String,
    val surahsCount: Int
)