package dev.sayed.mehrabalmomen.presentation.screen.quran

sealed interface SurahListEffect {
    data class NavigateToSurahAyat(val surahId: Int,val surahName:String) : SurahListEffect
}