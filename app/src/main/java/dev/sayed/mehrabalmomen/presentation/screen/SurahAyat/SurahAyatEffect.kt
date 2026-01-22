package dev.sayed.mehrabalmomen.presentation.screen.SurahAyat

import dev.sayed.mehrabalmomen.design_system.component.ToastDetails

sealed interface SurahAyatEffect {
    data class CopyAya(val text: String) : SurahAyatEffect
    data object NavigateToBack : SurahAyatEffect
    data class NavigateToSearch(
        val surahId: Int,
        val surahName: String
    ) : SurahAyatEffect

    data class ShowToast(val toast: ToastDetails) : SurahAyatEffect
}