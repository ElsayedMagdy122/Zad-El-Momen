package dev.sayed.mehrabalmomen.presentation.screen.settings

import dev.sayed.mehrabalmomen.design_system.component.ToastDetails

sealed interface SettingsEffect {
    data class ShowToast(val toast: ToastDetails) : SettingsEffect
    data class LaunchDonation(val productId: String) : SettingsEffect
    data object NavigateToLocation : SettingsEffect
    data object NavigateToHelpFeedback : SettingsEffect
    data object NavigateToRateApp : SettingsEffect
    data object NavigateToFAQ : SettingsEffect
    data object NavigateToPrivacy : SettingsEffect
    data object NavigateToContactUs : SettingsEffect
    data object NavigateToNotifications : SettingsEffect
}
