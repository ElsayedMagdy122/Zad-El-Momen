package dev.sayed.mehrabalmomen.presentation.navigation

import kotlinx.serialization.Serializable


@Serializable
sealed interface Route {
    @Serializable
    data object HomeScreen

    @Serializable
    data object FullPrayerTimeView

    @Serializable
    data object CalibrateDevice

    @Serializable
    data object QiblahScreen

    @Serializable
    data object LocationPermissionScreen

    @Serializable
    data object MadhabScreen

    @Serializable
    data object CalculationMethodScreen

    @Serializable
    data object SettingsScreen

    @Serializable
    data object MapsScreen
}