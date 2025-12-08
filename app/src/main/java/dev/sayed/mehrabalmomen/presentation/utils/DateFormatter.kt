package dev.sayed.mehrabalmomen.presentation.utils

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
fun format(instant: Instant, zone: TimeZone): String {
    val ldt = instant.toLocalDateTime(zone)
    val zoned = ldt.toJavaLocalDateTime().atZone(java.time.ZoneId.of(zone.id))

    val locale = java.util.Locale.getDefault()

    val formatter = java.time.format.DateTimeFormatter
        .ofPattern("hh:mm a", locale)
    return zoned.format(formatter)
}