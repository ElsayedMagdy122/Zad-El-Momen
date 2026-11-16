package dev.sayed.mehrabalmomen.presentation.utils

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
fun format(
    instant: Instant,
    zone: TimeZone
): FormattedTime {
    val ldt = instant.toLocalDateTime(zone)
    val hour12 = if (ldt.hour % 12 == 0) 12 else ldt.hour % 12
    val formattedTime = "${hour12.toString().padStart(2, '0')}:${ldt.minute.toString().padStart(2, '0')}"

    return FormattedTime(
        time = formattedTime,
        isAm = ldt.hour < 12
    )
}

data class FormattedTime(
    val time: String,
    val isAm: Boolean
)
