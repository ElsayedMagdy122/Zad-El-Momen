package dev.sayed.mehrabalmomen.presentation.screen.home

sealed interface HomeEffect {
    object NavigateToPrayerTimes : HomeEffect
    object NavigateToSettings : HomeEffect
    object NavigateToCalibrateDevice : HomeEffect
    object NavigateToQuran : HomeEffect
    object NavigateToTilawah : HomeEffect
    object NavigateToLocation : HomeEffect
    object RequestAlarmPermissions : HomeEffect
    object RequestNotificationPermission : HomeEffect
    object RequestExactAlarm : HomeEffect
    object RequestIgnoreBatteryOptimization : HomeEffect
}
