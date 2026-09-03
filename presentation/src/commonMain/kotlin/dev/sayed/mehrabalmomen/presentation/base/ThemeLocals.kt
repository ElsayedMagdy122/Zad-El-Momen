package dev.sayed.mehrabalmomen.presentation.base

import androidx.compose.runtime.compositionLocalOf
import dev.sayed.mehrabalmomen.domain.model.AppSettings

val LocalAppLocale = compositionLocalOf { AppSettings.Language.ARABIC }
val LocalIsDarkTheme = compositionLocalOf { false }

fun String.toLocalizedDigits(language: AppSettings.Language): String {
    return if (language == AppSettings.Language.ARABIC) {
        this.map {
            when (it) {
                in '0'..'9' -> '٠' + (it - '0')
                else -> it
            }
        }.joinToString("")
    } else {
        this
    }
}

fun String.localizeAmPm(language: AppSettings.Language): String {
    return if (language == AppSettings.Language.ARABIC) {
        this.replace("AM", "ص").replace("PM", "م")
    } else {
        this
    }
}
