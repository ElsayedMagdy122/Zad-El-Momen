package dev.sayed.mehrabalmomen.presentation.screen.quran.reciters

sealed interface RecitersEffect {
    data class ShowToast(val message: String) : RecitersEffect
    data object NavigateBack : RecitersEffect
    data class NavigateToRecitersSearch(val surahId: Int, val currentReaderId: Int? = null) : RecitersEffect
}
