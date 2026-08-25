package dev.sayed.mehrabalmomen.presentation.screen.onBoarding.batteryOptimization

sealed interface BatteryOptimizationEffect {
    object OpenSettings : BatteryOptimizationEffect
    object SkipForNow : BatteryOptimizationEffect
    object NavigateBack : BatteryOptimizationEffect
    object NavigateToLearnMore : BatteryOptimizationEffect
}