package dev.sayed.mehrabalmomen.presentation.screen.onBoarding.batteryOptimization

interface BatteryOptimizationInteractionListener {
    fun onOpenSettingsClicked()
    fun onOpenOverlaySettingsClicked() {}
    fun onSkipForNowClicked()
    fun onBackClicked()
    fun onLearnMoreClick()
}