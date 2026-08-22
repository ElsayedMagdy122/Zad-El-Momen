package dev.sayed.mehrabalmomen.domain.usecase

import dev.sayed.mehrabalmomen.domain.entity.prayer.PrayerCalculationError
import dev.sayed.mehrabalmomen.domain.entity.prayer.PrayerCalculationException
import dev.sayed.mehrabalmomen.domain.entity.widget.prayer.PrayerWidgetContent
import dev.sayed.mehrabalmomen.domain.entity.widget.prayer.PrayerWidgetError
import dev.sayed.mehrabalmomen.domain.entity.widget.prayer.PrayerWidgetSnapshot
import dev.sayed.mehrabalmomen.domain.repository.TimeRepository
import dev.sayed.mehrabalmomen.domain.repository.settings.SettingsRepository
import dev.sayed.mehrabalmomen.domain.repository.widget.ExactAlarmPermissionRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime

/**
 * Builds one self-consistent domain snapshot for the prayer widget.
 *
 * @property settingsRepository source of the latest saved prayer widget preferences.
 * @property timeRepository source of the instant and timezone captured for this refresh.
 * @property getPrayerTimelineUseCase calculator that selects the displayed and next prayers.
 * @property exactAlarmPermissionRepository platform boundary for exact alarm access.
 */
@OptIn(ExperimentalTime::class)
class GetPrayerWidgetSnapshotUseCase(
    private val settingsRepository: SettingsRepository,
    private val timeRepository: TimeRepository,
    private val getPrayerTimelineUseCase: GetPrayerTimelineUseCase,
    private val exactAlarmPermissionRepository: ExactAlarmPermissionRepository,
) {
    /**
     * Loads current widget settings, calculates the prayer timeline, and evaluates exact alarm
     * access to produce the state rendered by the widget.
     *
     * Location setup is checked before reading time or calculating prayers. Coroutine cancellation
     * is always propagated, while calculation and unexpected failures are converted into controlled
     * [PrayerWidgetSnapshot.Error] results.
     *
     * @return ready content, content with a permission requirement, a location requirement, or a
     * controlled error snapshot.
     */
    suspend operator fun invoke(): PrayerWidgetSnapshot {
        return try {
            val settings = settingsRepository.observePrayerWidgetSettings().first()
            if (!settings.isLocationConfigured) {
                return PrayerWidgetSnapshot.NeedsLocation
            }

            val timeContext = timeRepository.currentTimeContext()
            val timeline = getPrayerTimelineUseCase(
                settings = settings.prayerSettings,
                timeContext = timeContext,
            )
            val currentLocalDate = timeContext.instant.toLocalDateTime(timeContext.timeZone).date
            val content = PrayerWidgetContent(
                calculatedAt = timeContext.instant,
                timeZone = timeContext.timeZone,
                currentLocalDate = currentLocalDate,
                displayedDate = timeline.displayedDate,
                prayers = timeline.displayedPrayers,
                nextPrayer = timeline.nextPrayer,
                nextLocalMidnight = currentLocalDate
                    .plus(1, DateTimeUnit.DAY)
                    .atStartOfDayIn(timeContext.timeZone),
                countdownStartInstant = timeline.countdownStartInstant,
                remainingDuration = timeline.remainingDuration,
                location = settings.prayerSettings.location,
                language = settings.language,
            )

            if (exactAlarmPermissionRepository.canScheduleExactAlarms()) {
                PrayerWidgetSnapshot.Ready(content)
            } else {
                PrayerWidgetSnapshot.PermissionRequired(content)
            }
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: PrayerCalculationException) {
            PrayerWidgetSnapshot.Error(exception.toWidgetError())
        } catch (exception: Exception) {
            PrayerWidgetSnapshot.Error(PrayerWidgetError.UNKNOWN)
        }
    }

    /**
     * Maps a prayer calculation failure to the smaller error vocabulary supported by the widget.
     *
     * @return the widget error category corresponding to this calculation exception.
     */
    private fun PrayerCalculationException.toWidgetError(): PrayerWidgetError {
        return when (error) {
            PrayerCalculationError.INVALID_LATITUDE,
            PrayerCalculationError.INVALID_LONGITUDE -> PrayerWidgetError.INVALID_LOCATION
            PrayerCalculationError.CALCULATION_FAILED,
            PrayerCalculationError.INVALID_PRAYER_RESULT -> PrayerWidgetError.CALCULATION_FAILED
        }
    }
}
