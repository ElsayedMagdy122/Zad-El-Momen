package dev.sayed.mehrabalmomen.presentation.screen.AzkarDetails

sealed interface AzkarDetailEffect {
    object NavigateBack : AzkarDetailEffect
    data class ShowError(val message: String) : AzkarDetailEffect
}