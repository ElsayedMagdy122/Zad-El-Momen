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
}