package dev.sayed.mehrabalmomen.presentation.screen.prayers

interface FullPrayerTimesEffect {
    object NavigateBack : FullPrayerTimesEffect
    object RequestExactAlarm : FullPrayerTimesEffect
    object RequestIgnoreBatteryOptimization : FullPrayerTimesEffect
    object RequestXiaomiAutoStart : FullPrayerTimesEffect
}