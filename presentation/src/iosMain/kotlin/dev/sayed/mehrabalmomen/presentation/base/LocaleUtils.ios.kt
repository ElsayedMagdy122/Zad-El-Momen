package dev.sayed.mehrabalmomen.presentation.base

import dev.sayed.mehrabalmomen.domain.model.AppSettings
import platform.Foundation.NSUserDefaults

actual fun setAppLocale(language: AppSettings.Language) {
    NSUserDefaults.standardUserDefaults.setObject(listOf(language.code), forKey = "AppleLanguages")
    NSUserDefaults.standardUserDefaults.synchronize()
}
