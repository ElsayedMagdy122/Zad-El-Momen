package dev.sayed.mehrabalmomen.presentation.screen.quran.SurahList

data class SurahListUiState(
    val surahList: List<SurahUiState> = emptyList(),
    val isLoading: Boolean = true
)

data class SurahUiState(
    val id: Int,
    val name: String,
    val nameArabic: String,
    val nameEnglish: String,
    val ayahNumbers: Int,
    val surahType: SurahType,
    val surahImage: Int = 0
)

enum class SurahType {
    MAKKI,
    MADANI
}
