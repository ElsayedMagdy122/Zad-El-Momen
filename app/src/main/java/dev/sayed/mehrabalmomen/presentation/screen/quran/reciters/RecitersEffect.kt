package dev.sayed.mehrabalmomen.presentation.screen.quran.reciters

import dev.sayed.mehrabalmomen.design_system.component.ToastDetails
import dev.sayed.mehrabalmomen.presentation.screen.quran.SurahAyat.SurahAyatEffect

sealed interface RecitersEffect {
    data class ShowToast(val toast: ToastDetails) : RecitersEffect
    data class ReciterSelected(
        val readerId: Int,
        val nameAr: String,
        val nameEn: String
    ) : RecitersEffect
}