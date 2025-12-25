package dev.sayed.mehrabalmomen.domain.model

import dev.sayed.mehrabalmomen.domain.entity.CalculationMethod
import dev.sayed.mehrabalmomen.domain.entity.Madhab

data class AppSettings(
    val madhab: Madhab,
    val calculationMethod: CalculationMethod,
    val latitude: Double,
    val longitude: Double,
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
            madhab = Madhab.SHAFI,
            calculationMethod = CalculationMethod.MUSLIM_WORLD_LEAGUE,
            latitude = 0.0,
            longitude = 0.0,
            alarmsScheduled = false,
            theme = Theme.SYSTEM,
            language = Language.ARABIC
        )
    }
}