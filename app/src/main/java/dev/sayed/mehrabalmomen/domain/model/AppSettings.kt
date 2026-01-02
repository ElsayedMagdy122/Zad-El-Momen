package dev.sayed.mehrabalmomen.domain.model

import dev.sayed.mehrabalmomen.domain.entity.CalculationMethod
import dev.sayed.mehrabalmomen.domain.entity.Location
import dev.sayed.mehrabalmomen.domain.entity.Madhab

data class AppSettings(
    val prayerSettings: PrayerSettings,
    val alarmsScheduled: Boolean,
    val theme: Theme = Theme.SYSTEM,
    val language: Language = Language.ARABIC
) {
    enum class Theme {
        LIGHT,
        DARK,
        SYSTEM
    }

    enum class Language(val code: String) {
        ENGLISH("en"),
        ARABIC("ar")
    }

    companion object {
        val default = AppSettings(
            prayerSettings = PrayerSettings(
                madhab = Madhab.SHAFI,
                calculationMethod = CalculationMethod.EGYPTIAN,
                location = Location(
                    latitude = 0.0,
                    longitude = 0.0,
                    country = "Unknown",
                    state = "Unknown"
                )
            ),
            alarmsScheduled = false,
            theme = Theme.SYSTEM,
            language = Language.ARABIC
        )
    }
}