package dev.sayed.mehrabalmomen.domain.entity.prayer

import kotlinx.datetime.LocalDate
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Describes the prayer schedule that should be displayed for one timeline calculation.
 *
 * @property displayedDate local calendar date represented by [displayedPrayers]. This is today
 * when a prayer remains today, or tomorrow after today's Isha has been reached.
 * @property displayedPrayers chronologically ordered prayers for [displayedDate].
 * @property nextPrayer first prayer whose absolute instant is strictly later than the calculation
 * instant.
 * @property countdownStartInstant previous prayer boundary from which countdown progress begins.
 * @property remainingDuration non-negative duration from the calculation instant to [nextPrayer].
 */
@OptIn(ExperimentalTime::class)
data class PrayerTimelineResult(
    val displayedDate: LocalDate,
    val displayedPrayers: List<Prayer>,
    val nextPrayer: Prayer,
    val countdownStartInstant: Instant,
    val remainingDuration: Duration,
) {
    /**
     * Determines whether [prayer] is the exact upcoming prayer represented by this result.
     *
     * Both the prayer name and absolute instant are compared through [Prayer] equality. Comparing
     * only the name would incorrectly mark today's Fajr as upcoming when [nextPrayer] is tomorrow's
     * Fajr.
     *
     * @param prayer prayer row or item whose upcoming state is required.
     * @return `true` only when [prayer] has the same name and instant as [nextPrayer].
     */
    fun isUpcoming(prayer: Prayer): Boolean = prayer == nextPrayer
}
