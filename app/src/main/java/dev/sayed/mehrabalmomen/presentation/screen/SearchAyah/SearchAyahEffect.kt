package dev.sayed.mehrabalmomen.presentation.screen.SearchAyah

sealed interface SearchAyahEffect {
    object NavigateBack : SearchAyahEffect
    data class NavigateToSurah(val surahId: Int, val surahName: String, val ayahId: Int) :
        SearchAyahEffect
}