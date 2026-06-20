package dev.sayed.mehrabalmomen.domain.model

import dev.sayed.mehrabalmomen.domain.entity.prayer.PrayerSettings

data class AppSettings(
    val prayerSettings: PrayerSettings,
    val alarmsScheduled: Boolean,
    val theme: Theme = Theme.SYSTEM,
    val language: Language = Language.ARABIC,
    val readingMode: ReadingMode
) {
    enum class Theme {
        LIGHT,
        DARK,
        SYSTEM
    }

    enum class ReadingMode {
        CONTINUOUS_READING,
        PAGE_VIEW;
    }

    enum class Language(val code: String) {
        ENGLISH("en"),
        ARABIC("ar")
    }
}