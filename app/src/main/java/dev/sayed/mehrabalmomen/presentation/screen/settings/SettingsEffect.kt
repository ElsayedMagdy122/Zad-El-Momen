package dev.sayed.mehrabalmomen.presentation.screen.settings

import dev.sayed.mehrabalmomen.design_system.component.ToastDetails

sealed interface SettingsEffect {
    object NavigateToLocation : SettingsEffect
    object NavigateToHelpFeedback : SettingsEffect
    object NavigateToRateApp : SettingsEffect
    object NavigateToAbout : SettingsEffect
    data class LaunchDonation(val productId: String) : SettingsEffect
    data class ShowToast(val toast: ToastDetails) : SettingsEffect
}