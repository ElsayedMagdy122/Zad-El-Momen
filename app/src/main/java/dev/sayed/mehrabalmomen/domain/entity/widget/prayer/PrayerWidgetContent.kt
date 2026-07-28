package dev.sayed.mehrabalmomen.domain.entity.widget.prayer

import dev.sayed.mehrabalmomen.domain.entity.location.Location
import dev.sayed.mehrabalmomen.domain.entity.prayer.Prayer
import dev.sayed.mehrabalmomen.domain.model.AppSettings
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Successfully calculated domain content needed to render the prayer widget.
 *
 * @property calculatedAt instant at which this snapshot was produced.
 * @property timeZone timezone used for all local-time conversions in the snapshot.
 * @property currentLocalDate local date at [calculatedAt], used to detect tomorrow rollover.
 * @property displayedDate date whose prayer list is displayed by the widget.
 * @property prayers ordered prayers belonging to [displayedDate].
 * @property nextPrayer first prayer strictly after [calculatedAt].
 * @property countdownStartInstant previous prayer boundary used as zero ring progress.
 * @property remainingDuration duration from [calculatedAt] until [nextPrayer].
 * @property location location used by the prayer calculation.
 * @property language language that presentation code should use for labels and digits.
 */
@OptIn(ExperimentalTime::class)
data class PrayerWidgetContent(
    val calculatedAt: Instant,
    val timeZone: TimeZone,
    val currentLocalDate: LocalDate,
    val displayedDate: LocalDate,
    val prayers: List<Prayer>,
    val nextPrayer: Prayer,
    val countdownStartInstant: Instant,
    val remainingDuration: Duration,
    val location: Location,
    val language: AppSettings.Language,
)
