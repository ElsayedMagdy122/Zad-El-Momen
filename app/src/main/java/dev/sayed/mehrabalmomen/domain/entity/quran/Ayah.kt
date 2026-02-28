package dev.sayed.mehrabalmomen.domain.entity.quran

data class Ayah(
    val id: Int,
    val ayahNumber: Int,
    val juzNumber: Int,
    val surahNumber: Int,
    val surahNameArabic: String,
    val surahNameEnglish: String,
    val text: String
)