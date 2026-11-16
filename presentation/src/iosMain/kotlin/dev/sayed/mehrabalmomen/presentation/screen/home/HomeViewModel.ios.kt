package dev.sayed.mehrabalmomen.presentation.screen.home

import dev.sayed.mehrabalmomen.domain.model.AppSettings

actual fun getHijriDateString(language: AppSettings.Language): String {
    return if (language == AppSettings.Language.ARABIC) "1448 هـ" else "1448 AH"
}
