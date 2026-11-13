package dev.sayed.mehrabalmomen.presentation.screen.AzkarDetails

import dev.sayed.mehrabalmomen.design_system.component.ToastDetails

sealed interface AzkarDetailEffect {
    object NavigateBack : AzkarDetailEffect
    data class ShowError(val message: String) : AzkarDetailEffect
    data class CopyText(val text: String) : AzkarDetailEffect
    data class ShowToast(val toast: ToastDetails) : AzkarDetailEffect
}