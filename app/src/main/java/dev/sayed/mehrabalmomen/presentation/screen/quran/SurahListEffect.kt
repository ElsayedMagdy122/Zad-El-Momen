package dev.sayed.mehrabalmomen.presentation.screen.quran

sealed interface SurahListEffect {
    data class NavigateToSurahAyat(val surahId: Int,val arabicName:String, val englishName:String) : SurahListEffect
    data object NavigateToQuranSearch : SurahListEffect
    data object NavigateToBookmarksList : SurahListEffect
}