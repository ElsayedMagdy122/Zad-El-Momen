package dev.sayed.mehrabalmomen.domain.entity.quran

data class Surah(
    val surahNumber: Int,
    val nameArabic: String,
    val nameEnglish: String,
    val ayahCount: Int,
    val type: SurahType
) {
    enum class SurahType {
        MAKKI,
        MADANI
    }
}