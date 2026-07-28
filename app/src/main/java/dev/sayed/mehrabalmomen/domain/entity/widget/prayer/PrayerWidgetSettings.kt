package dev.sayed.mehrabalmomen.domain.entity.widget.prayer

import dev.sayed.mehrabalmomen.domain.entity.prayer.PrayerSettings
import dev.sayed.mehrabalmomen.domain.model.AppSettings

/**
 * User preferences required to calculate and present a prayer widget snapshot.
 *
 * @property prayerSettings Madhab, calculation method, and saved calculation location.
 * @property language language used to localize the widget presentation.
 * @property isLocationConfigured whether the user deliberately completed location setup.
 */
data class PrayerWidgetSettings(
    val prayerSettings: PrayerSettings,
    val language: AppSettings.Language,
    val isLocationConfigured: Boolean,
)
