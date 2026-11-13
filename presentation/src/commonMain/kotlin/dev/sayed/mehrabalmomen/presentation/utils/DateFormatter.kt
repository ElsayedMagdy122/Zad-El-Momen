package dev.sayed.mehrabalmomen.presentation.utils

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

fun format(
    instant: Instant,
    zone: TimeZone
): FormattedTime {
    val ldt = instant.toLocalDateTime(zone)
    val rawHour = ldt.hour
    val isAm = rawHour < 12
    val hour12 = when (val h = rawHour % 12) {
        0 -> 12
        else -> h
    }
    val hourStr = hour12.toString().padStart(2, '0')
    val minuteStr = ldt.minute.toString().padStart(2, '0')

    return FormattedTime(
        time = "$hourStr:$minuteStr",
        isAm = isAm
    )
}

data class FormattedTime(
    val time: String,
    val isAm: Boolean
)
