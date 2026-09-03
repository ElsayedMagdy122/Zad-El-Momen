package dev.sayed.mehrabalmomen.presentation.utils

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime
import kotlinx.datetime.Instant
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalTime::class)
actual fun format(
    instant: Instant,
    zone: TimeZone
): FormattedTime {
    val ldt = instant.toLocalDateTime(zone)
    val zoned = ldt.toJavaLocalDateTime()
        .atZone(java.time.ZoneId.of(zone.id))
    val locale = Locale.getDefault()

    val timeFormatter = DateTimeFormatter
        .ofPattern("hh:mm", locale)

    val formattedTime = zoned.format(timeFormatter)

    return FormattedTime(
        time = formattedTime,
        isAm = zoned.hour < 12
    )
}
