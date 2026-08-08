package dev.sayed.mehrabalmomen.presentation.screen.quran.reciters

import dev.sayed.mehrabalmomen.design_system.component.ToastDetails
import dev.sayed.mehrabalmomen.presentation.screen.quran.SurahAyat.SurahAyatEffect

sealed interface RecitersEffect {
    data class ShowToast(val toast: ToastDetails) : RecitersEffect
    data object NavigateBack : RecitersEffect
    data class NavigateToRecitersSearch(val surahId: Int, val currentReaderId: Int? = null) : RecitersEffect
}