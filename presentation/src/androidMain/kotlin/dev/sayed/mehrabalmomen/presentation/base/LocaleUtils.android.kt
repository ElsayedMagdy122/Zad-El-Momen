package dev.sayed.mehrabalmomen.presentation.base

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import dev.sayed.mehrabalmomen.domain.model.AppSettings
import java.util.Locale

actual fun setAppLocale(language: AppSettings.Language) {
    val locale = Locale.forLanguageTag(language.code)
    Locale.setDefault(locale)
    val appLocale = LocaleListCompat.forLanguageTags(language.code)
    AppCompatDelegate.setApplicationLocales(appLocale)
}
