package dev.sayed.mehrabalmomen.presentation.widget.prayer.mapper

import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.domain.entity.prayer.Prayer
import dev.sayed.mehrabalmomen.domain.entity.widget.prayer.PrayerWidgetContent
import dev.sayed.mehrabalmomen.domain.entity.widget.prayer.PrayerWidgetSnapshot
import dev.sayed.mehrabalmomen.domain.model.AppSettings
import dev.sayed.mehrabalmomen.presentation.widget.prayer.PrayerWidgetPrayer
import dev.sayed.mehrabalmomen.presentation.widget.prayer.PrayerWidgetStatus
import dev.sayed.mehrabalmomen.presentation.widget.prayer.PrayerWidgetUiState
import dev.sayed.mehrabalmomen.presentation.widget.prayer.calculatePrayerWidgetCountdownProgress
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toLocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

/** Converts domain prayer widget snapshots into fully formatted presentation state. */
@OptIn(ExperimentalTime::class)
class PrayerWidgetSnapshotMapper {
    /**
     * Maps a domain snapshot to the matching UI status and formatted content.
     *
     * @param snapshot domain result produced for the current widget refresh.
     * @return presentation state ready for the Glance widget renderer.
     */
    fun map(snapshot: PrayerWidgetSnapshot): PrayerWidgetUiState {
        return when (snapshot) {
            is PrayerWidgetSnapshot.Ready -> snapshot.content.toUiState(PrayerWidgetStatus.READY)
            is PrayerWidgetSnapshot.PermissionRequired -> snapshot.content.toUiState(
                PrayerWidgetStatus.EXACT_ALARM_PERMISSION_REQUIRED,
            )
            PrayerWidgetSnapshot.NeedsLocation -> PrayerWidgetUiState(
                status = PrayerWidgetStatus.NEEDS_LOCATION,
            )
            is PrayerWidgetSnapshot.Error -> PrayerWidgetUiState(
                status = PrayerWidgetStatus.ERROR,
            )
        }
    }

    /**
     * Formats successful domain content while preserving the supplied presentation status.
     *
     * @param status ready or permission-required status associated with this content.
     * @return localized UI state containing prayer rows, countdown data, and date metadata.
     */
    private fun PrayerWidgetContent.toUiState(status: PrayerWidgetStatus): PrayerWidgetUiState {
        return PrayerWidgetUiState(
            status = status,
            nextPrayerName = nextPrayer.name.localizedName(language),
            nextPrayerTime = nextPrayer.formattedTime(timeZone, language),
            countdown = remainingDuration.formattedCountdown(language),
            countdownStartEpochMillis = countdownStartInstant.toEpochMilliseconds(),
            targetEpochMillis = nextPrayer.time.toEpochMilliseconds(),
            countdownProgress = calculatePrayerWidgetCountdownProgress(
                startEpochMillis = countdownStartInstant.toEpochMilliseconds(),
                targetEpochMillis = nextPrayer.time.toEpochMilliseconds(),
                currentEpochMillis = calculatedAt.toEpochMilliseconds(),
            ),
            displayedDate = displayedDate.toJavaLocalDate().format(dateFormatter(language)),
            timeZoneId = timeZone.id,
            languageCode = language.code,
            isRtl = language == AppSettings.Language.ARABIC,
            prayers = prayers.map { prayer ->
                PrayerWidgetPrayer(
                    name = prayer.name.localizedName(language),
                    time = prayer.formattedTime(timeZone, language),
                    iconRes = prayer.name.iconRes(),
                    isUpcoming = prayer == nextPrayer,
                )
            },
            isTomorrow = displayedDate > currentLocalDate,
        )
    }

    /**
     * Formats this prayer's instant as a localized 12-hour clock value.
     *
     * @param timeZone timezone used to convert the absolute prayer instant to local time.
     * @param language language used for the period label and numeric digits.
     * @return a value in `hh:mm AM/PM` form, localized for [language].
     */
    private fun Prayer.formattedTime(
        timeZone: TimeZone,
        language: AppSettings.Language,
    ): String {
        val localTime = time.toLocalDateTime(timeZone).time
        val hour = localTime.hour
        val hour12 = when (val remainder = hour % 12) {
            0 -> 12
            else -> remainder
        }
        val period = if (hour < 12) {
            language.amText()
        } else {
            language.pmText()
        }
        return String.format(Locale.US, "%02d:%02d %s", hour12, localTime.minute, period)
            .localizedDigits(language)
    }

    /**
     * Formats this duration as a non-negative localized widget countdown.
     *
     * @param language language used to select Latin or Arabic-Indic digits.
     * @return the remaining time in `HH:mm:ss` form.
     */
    private fun Duration.formattedCountdown(language: AppSettings.Language): String {
        val totalSeconds = inWholeSeconds.coerceAtLeast(0)
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        return String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds)
            .localizedDigits(language)
    }

    /**
     * Creates the date formatter used for the selected widget language.
     *
     * @param language language whose locale should control date formatting.
     * @return a formatter that emits the widget's `yyyy-MM-dd` date representation.
     */
    private fun dateFormatter(language: AppSettings.Language): DateTimeFormatter {
        val locale = when (language) {
            AppSettings.Language.ENGLISH -> Locale.ENGLISH
            AppSettings.Language.ARABIC -> Locale.Builder().setLanguage("ar").build()
        }
        return DateTimeFormatter.ofPattern("yyyy-MM-dd", locale)
    }

    /**
     * Converts this domain prayer name into its localized display label.
     *
     * @param language language requested by the user's saved settings.
     * @return the English or Arabic prayer name.
     */
    private fun Prayer.PrayerName.localizedName(language: AppSettings.Language): String {
        return when (language) {
            AppSettings.Language.ENGLISH -> when (this) {
                Prayer.PrayerName.FAJR -> "Fajr"
                Prayer.PrayerName.ZUHR -> "Dhuhr"
                Prayer.PrayerName.ASR -> "Asr"
                Prayer.PrayerName.MAGHRIB -> "Maghrib"
                Prayer.PrayerName.ISHA -> "Isha"
            }
            AppSettings.Language.ARABIC -> when (this) {
                Prayer.PrayerName.FAJR -> "الفجر"
                Prayer.PrayerName.ZUHR -> "الظهر"
                Prayer.PrayerName.ASR -> "العصر"
                Prayer.PrayerName.MAGHRIB -> "المغرب"
                Prayer.PrayerName.ISHA -> "العشاء"
            }
        }
    }

    /**
     * Selects the widget drawable associated with this prayer name.
     *
     * @return drawable resource identifier used by the corresponding prayer row.
     */
    private fun Prayer.PrayerName.iconRes(): Int {
        return when (this) {
            Prayer.PrayerName.FAJR -> R.drawable.shalat_shubuh
            Prayer.PrayerName.ZUHR -> R.drawable.shalat_zhuhur
            Prayer.PrayerName.ASR -> R.drawable.shalat_ashar
            Prayer.PrayerName.MAGHRIB -> R.drawable.shalat_maghrib
            Prayer.PrayerName.ISHA -> R.drawable.shalat_isya
        }
    }

    /**
     * Returns the localized ante-meridiem label for this language.
     *
     * @return `AM` in English or its Arabic widget label.
     */
    private fun AppSettings.Language.amText(): String {
        return when (this) {
            AppSettings.Language.ENGLISH -> "AM"
            AppSettings.Language.ARABIC -> "صباحا"
        }
    }

    /**
     * Returns the localized post-meridiem label for this language.
     *
     * @return `PM` in English or its Arabic widget label.
     */
    private fun AppSettings.Language.pmText(): String {
        return when (this) {
            AppSettings.Language.ENGLISH -> "PM"
            AppSettings.Language.ARABIC -> "مساء"
        }
    }

    /**
     * Replaces Latin digits in this string with Arabic-Indic digits when Arabic is selected.
     *
     * @param language language that determines whether digit conversion is required.
     * @return the original string for English or an Arabic-digit copy for Arabic.
     */
    private fun String.localizedDigits(language: AppSettings.Language): String {
        if (language != AppSettings.Language.ARABIC) return this

        return map { char ->
            when (char) {
                in '0'..'9' -> '٠' + (char - '0')
                else -> char
            }
        }.joinToString(separator = "")
    }

}
