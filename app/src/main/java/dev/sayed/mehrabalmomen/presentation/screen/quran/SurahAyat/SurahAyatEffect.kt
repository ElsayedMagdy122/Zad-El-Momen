package dev.sayed.mehrabalmomen.presentation.screen.quran.SurahAyat

import dev.sayed.mehrabalmomen.design_system.component.ToastDetails

sealed interface SurahAyatEffect {
    data class CopyAya(val text: String) : SurahAyatEffect
    data object NavigateToBack : SurahAyatEffect
    data class NavigateToReciters(val surahId: Int, val currentReaderId: Int? = null) : SurahAyatEffect
    data class NavigateToSearch(
        val surahId: Int,
        val arabicName: String,
        val englishName: String,
    ) : SurahAyatEffect

    data class ShowToast(val toast: ToastDetails) : SurahAyatEffect
}