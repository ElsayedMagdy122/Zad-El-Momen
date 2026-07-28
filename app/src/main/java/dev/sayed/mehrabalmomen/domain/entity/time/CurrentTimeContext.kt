package dev.sayed.mehrabalmomen.domain.entity.time

import kotlinx.datetime.TimeZone
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Immutable clock snapshot used to keep one calculation internally consistent.
 *
 * @property instant absolute point in time captured for the calculation.
 * @property timeZone timezone captured alongside [instant] for local date conversion.
 */
@OptIn(ExperimentalTime::class)
data class CurrentTimeContext(
    val instant: Instant,
    val timeZone: TimeZone,
)
