package dev.sayed.mehrabalmomen.presentation.screen.prayers

interface PrayerTimesEffect {
    object NavigateBack : PrayerTimesEffect
    object RequestExactAlarm : PrayerTimesEffect
    object RequestIgnoreBatteryOptimization : PrayerTimesEffect
    object RequestNotificationPermission : PrayerTimesEffect
    object ShowBatteryOptimizationDialog : PrayerTimesEffect
}
