package dev.sayed.mehrabalmomen.presentation.screen.quran

data class SurahListUiState(
    val surahList: List<SurahUiState> = emptyList()
)
data class SurahUiState(
    val id:Int,
    val name: String,
    val nameArabic: String,
    val nameEnglish: String,
    val ayahNumbers: Int,
    val surahType: String,
    val surahImage: Int
)