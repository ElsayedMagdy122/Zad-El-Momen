package dev.sayed.mehrabalmomen.presentation.screen.settings

sealed interface SettingsEffect {
    object NavigateToLocation : SettingsEffect
    object NavigateToHelpFeedback : SettingsEffect
    object NavigateToRateApp : SettingsEffect
    object NavigateToAbout : SettingsEffect
}