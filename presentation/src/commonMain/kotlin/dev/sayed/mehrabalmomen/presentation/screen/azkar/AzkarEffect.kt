package dev.sayed.mehrabalmomen.presentation.screen.azkar

sealed interface AzkarEffect {
    data class NavigateToDetails(val type: AzkarType) : AzkarEffect
    object NavigateToBack : AzkarEffect
    data class ShowError(val message: String) : AzkarEffect
}