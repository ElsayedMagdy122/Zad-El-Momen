package dev.sayed.mehrabalmomen.presentation.utils

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
fun format(
    instant: Instant,
    zone: TimeZone
): FormattedTime {

    val ldt = instant.toLocalDateTime(zone)
    val zoned = ldt.toJavaLocalDateTime()
        .atZone(java.time.ZoneId.of(zone.id))
    val locale = java.util.Locale.getDefault()

    val timeFormatter = java.time.format.DateTimeFormatter
        .ofPattern("hh:mm", locale)

    val formattedTime = zoned.format(timeFormatter)

    return FormattedTime(
        time = formattedTime,
        isAm = zoned.hour < 12
    )
}
data class FormattedTime(
    val time: String,
    val isAm: Boolean
)