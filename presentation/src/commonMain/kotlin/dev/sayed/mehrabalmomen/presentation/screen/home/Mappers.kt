package dev.sayed.mehrabalmomen.presentation.screen.home

import dev.sayed.mehrabalmomen.domain.entity.prayer.Prayer
import kotlinx.datetime.TimeZone
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun Prayer.toPrayerUiState(zone: TimeZone): HomeUiState.PrayerUiState{
    return HomeUiState.PrayerUiState()
}
