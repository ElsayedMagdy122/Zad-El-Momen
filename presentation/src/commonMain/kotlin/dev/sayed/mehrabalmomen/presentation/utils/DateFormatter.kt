package dev.sayed.mehrabalmomen.presentation.utils

import kotlinx.datetime.TimeZone
import kotlinx.datetime.Instant
import kotlin.time.ExperimentalTime

data class FormattedTime(
    val time: String,
    val isAm: Boolean
)

@OptIn(ExperimentalTime::class)
expect fun format(
    instant: Instant,
    zone: TimeZone
): FormattedTime
