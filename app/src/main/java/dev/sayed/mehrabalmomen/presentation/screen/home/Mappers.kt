package dev.sayed.mehrabalmomen.presentation.screen.home

import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.domain.entity.Prayer
import dev.sayed.mehrabalmomen.presentation.utils.format
import dev.sayed.mehrabalmomen.presentation.utils.toUiIcon
import dev.sayed.mehrabalmomen.presentation.utils.toUiName
import kotlinx.datetime.TimeZone
import kotlin.time.ExperimentalTime


@OptIn(ExperimentalTime::class)
fun Prayer.toPrayerUiState(zone: TimeZone): HomeUiState.PrayerUiState{
    return HomeUiState.PrayerUiState(
        name = this.toUiName(this.name),
        time = format(instant = this.time, zone = zone),
        icon = this.toUiIcon(this.name)
    )
}

