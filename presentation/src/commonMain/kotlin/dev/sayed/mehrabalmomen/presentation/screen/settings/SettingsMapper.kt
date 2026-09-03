package dev.sayed.mehrabalmomen.presentation.screen.settings

import SettingsUiState
import dev.sayed.mehrabalmomen.domain.entity.prayer.CalculationMethod
import dev.sayed.mehrabalmomen.domain.entity.prayer.Madhab
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
    SettingsUiState.MadhabState.HANAFI -> Madhab.HANAFI
}

fun SettingsUiState.CalculationMethod.toDomain(): CalculationMethod {
    return when (this) {
        SettingsUiState.CalculationMethod.MUSLIM_WORLD_LEAGUE -> CalculationMethod.MUSLIM_WORLD_LEAGUE
        SettingsUiState.CalculationMethod.EGYPTIAN -> CalculationMethod.EGYPTIAN
        SettingsUiState.CalculationMethod.KARACHI -> CalculationMethod.KARACHI
        SettingsUiState.CalculationMethod.UMM_AL_QURA -> CalculationMethod.UMM_AL_QURA
        SettingsUiState.CalculationMethod.DUBAI -> CalculationMethod.DUBAI
        SettingsUiState.CalculationMethod.QATAR -> CalculationMethod.QATAR
        SettingsUiState.CalculationMethod.KUWAIT -> CalculationMethod.KUWAIT
        SettingsUiState.CalculationMethod.MOONSIGHTING_COMMITTEE -> CalculationMethod.MOONSIGHTING_COMMITTEE
        SettingsUiState.CalculationMethod.SINGAPORE -> CalculationMethod.SINGAPORE
        SettingsUiState.CalculationMethod.NORTH_AMERICA -> CalculationMethod.NORTH_AMERICA
    }
}

fun CalculationMethod.toUi(): SettingsUiState.CalculationMethod {
    return when (this) {
        CalculationMethod.MUSLIM_WORLD_LEAGUE ->
            SettingsUiState.CalculationMethod.MUSLIM_WORLD_LEAGUE

        CalculationMethod.EGYPTIAN ->
            SettingsUiState.CalculationMethod.EGYPTIAN

        CalculationMethod.KARACHI ->
            SettingsUiState.CalculationMethod.KARACHI

        CalculationMethod.UMM_AL_QURA ->
            SettingsUiState.CalculationMethod.UMM_AL_QURA

        CalculationMethod.DUBAI ->
            SettingsUiState.CalculationMethod.DUBAI

        CalculationMethod.QATAR ->
            SettingsUiState.CalculationMethod.QATAR

        CalculationMethod.KUWAIT ->
            SettingsUiState.CalculationMethod.KUWAIT

        CalculationMethod.MOONSIGHTING_COMMITTEE ->
            SettingsUiState.CalculationMethod.MOONSIGHTING_COMMITTEE

        CalculationMethod.SINGAPORE ->
            SettingsUiState.CalculationMethod.SINGAPORE

        CalculationMethod.NORTH_AMERICA ->
            SettingsUiState.CalculationMethod.NORTH_AMERICA

        CalculationMethod.OTHER -> SettingsUiState.CalculationMethod.MUSLIM_WORLD_LEAGUE
    }
}