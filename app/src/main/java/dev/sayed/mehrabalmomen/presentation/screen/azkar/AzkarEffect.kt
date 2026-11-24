package dev.sayed.mehrabalmomen.presentation.screen.azkar

sealed interface AzkarEffect {
    data class NavigateToDetails(val title: String) : AzkarEffect
    data class ShowError(val message: String) : AzkarEffect
    data object NavigateToBack : AzkarEffect
}