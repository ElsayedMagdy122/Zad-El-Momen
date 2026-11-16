package dev.sayed.mehrabalmomen.presentation.screen.prayers

sealed interface PrayerTimesEffect {
    data object NavigateBack : PrayerTimesEffect
    data object RequestExactAlarm : PrayerTimesEffect
    data object RequestIgnoreBatteryOptimization : PrayerTimesEffect
    data object RequestNotificationPermission : PrayerTimesEffect
    data object RequestOverlayPermission : PrayerTimesEffect
    data object OpenAppSettings : PrayerTimesEffect
    data object ShowBatteryOptimizationDialog : PrayerTimesEffect
}
