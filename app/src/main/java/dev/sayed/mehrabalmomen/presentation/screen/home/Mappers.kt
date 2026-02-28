package dev.sayed.mehrabalmomen.presentation.screen.home

import dev.sayed.mehrabalmomen.domain.entity.prayer.Prayer
import dev.sayed.mehrabalmomen.presentation.utils.format
import dev.sayed.mehrabalmomen.presentation.utils.toUiIcon
import dev.sayed.mehrabalmomen.presentation.utils.toUiName
import kotlinx.datetime.TimeZone
import kotlin.time.ExperimentalTime


@OptIn(ExperimentalTime::class)
fun Prayer.toPrayerUiState(zone: TimeZone): HomeUiState.PrayerUiState{
    val formatted = format(instant = this.time, zone = zone)
    return HomeUiState.PrayerUiState(
        name = this.toUiName(this.name),
        time = formatted.time,
        isAm = formatted.isAm,
        icon = this.toUiIcon(this.name)
    )
}

