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
}