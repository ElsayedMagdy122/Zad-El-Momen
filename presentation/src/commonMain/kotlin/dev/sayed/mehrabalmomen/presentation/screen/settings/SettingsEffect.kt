package dev.sayed.mehrabalmomen.presentation.screen.settings

sealed interface SettingsEffect {
    data class ShowToast(val message: String) : SettingsEffect
    data class LaunchDonation(val productId: String) : SettingsEffect
    data object NavigateToLocation : SettingsEffect
    data object NavigateToHelpFeedback : SettingsEffect
    data object NavigateToRateApp : SettingsEffect
    data object NavigateToFAQ : SettingsEffect
    data object NavigateToPrivacy : SettingsEffect
    data object NavigateToContactUs : SettingsEffect
    data object NavigateToNotifications : SettingsEffect
    data object RequestPinWidget : SettingsEffect
}
