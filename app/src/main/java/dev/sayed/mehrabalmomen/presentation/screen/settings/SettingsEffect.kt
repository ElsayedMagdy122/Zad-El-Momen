package dev.sayed.mehrabalmomen.presentation.screen.settings

import dev.sayed.mehrabalmomen.design_system.component.ToastDetails

sealed interface SettingsEffect {
    object NavigateToLocation : SettingsEffect
    object NavigateToHelpFeedback : SettingsEffect
    object NavigateToRateApp : SettingsEffect
    object NavigateToAbout : SettingsEffect
    object NavigateToPrivacy : SettingsEffect
    object NavigateToFAQ : SettingsEffect
    object NavigateToContactUs : SettingsEffect
    object NavigateToNotifications : SettingsEffect
    data class LaunchDonation(val productId: String) : SettingsEffect
    data class ShowToast(val toast: ToastDetails) : SettingsEffect
}