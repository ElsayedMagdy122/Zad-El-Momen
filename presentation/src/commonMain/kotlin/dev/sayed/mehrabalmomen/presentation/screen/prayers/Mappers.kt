package dev.sayed.mehrabalmomen.presentation.screen.prayers

import dev.sayed.mehrabalmomen.domain.entity.prayer.Prayer
import kotlinx.datetime.TimeZone
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun Prayer.toPrayerUiState(zone: TimeZone): PrayerTimesUiState.PrayerUiState {
    return PrayerTimesUiState.PrayerUiState()
}
