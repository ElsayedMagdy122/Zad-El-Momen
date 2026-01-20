package dev.sayed.mehrabalmomen.presentation.screen.quran

import dev.sayed.mehrabalmomen.R

data class SurahListUiState(
    val surahList: List<SurahUiState> = emptyList()
)
data class SurahUiState(
    val id:Int,
    val name: String,
    val nameArabic: String,
    val nameEnglish: String,
    val ayahNumbers: Int,
    val surahType: SurahType,
    val surahImage: Int
)
enum class SurahType(val text:Int) {
    MAKKI(R.string.mekki),
    MADANI(R.string.madani)
}