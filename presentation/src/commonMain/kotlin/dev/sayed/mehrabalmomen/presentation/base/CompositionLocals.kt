package dev.sayed.mehrabalmomen.presentation.base

import androidx.compose.runtime.compositionLocalOf
import dev.sayed.mehrabalmomen.domain.model.AppSettings

val LocalAppLocale = compositionLocalOf { AppSettings.Language.ARABIC }
val LocalIsDarkTheme = compositionLocalOf { false }
