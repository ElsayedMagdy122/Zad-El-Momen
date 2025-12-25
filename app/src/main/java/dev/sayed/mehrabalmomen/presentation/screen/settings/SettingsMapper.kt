package dev.sayed.mehrabalmomen.presentation.screen.settings

import dev.sayed.mehrabalmomen.domain.entity.Madhab
import dev.sayed.mehrabalmomen.domain.model.AppSettings

fun AppSettings.Language.toUi(): SettingsUiState.Language = when (this) {
    AppSettings.Language.ENGLISH -> SettingsUiState.Language.ENGLISH
    AppSettings.Language.ARABIC -> SettingsUiState.Language.ARABIC
}

fun SettingsUiState.Language.toDomain(): AppSettings.Language = when (this) {
    SettingsUiState.Language.ENGLISH -> AppSettings.Language.ENGLISH
    SettingsUiState.Language.ARABIC -> AppSettings.Language.ARABIC
}


fun AppSettings.Theme.toUi(): SettingsUiState.ThemeState = when (this) {
    AppSettings.Theme.LIGHT -> SettingsUiState.ThemeState.LIGHT
    AppSettings.Theme.DARK -> SettingsUiState.ThemeState.DARK
    AppSettings.Theme.SYSTEM -> SettingsUiState.ThemeState.SYSTEM
}

fun SettingsUiState.ThemeState.toDomain(): AppSettings.Theme = when (this) {
    SettingsUiState.ThemeState.LIGHT -> AppSettings.Theme.LIGHT
    SettingsUiState.ThemeState.DARK -> AppSettings.Theme.DARK
    SettingsUiState.ThemeState.SYSTEM -> AppSettings.Theme.SYSTEM
}


fun Madhab.toUi(): SettingsUiState.MadhabState = when (this) {
    Madhab.SHAFI -> SettingsUiState.MadhabState.SHAFI
    Madhab.HANAFI -> SettingsUiState.MadhabState.HANAFI
}

fun SettingsUiState.MadhabState.toDomain(): Madhab = when (this) {
    SettingsUiState.MadhabState.SHAFI -> Madhab.SHAFI
    SettingsUiState.MadhabState.HANAFI ->Madhab.HANAFI
}