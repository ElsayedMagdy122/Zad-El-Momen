package dev.sayed.mehrabalmomen.presentation.navigation

import dev.sayed.mehrabalmomen.presentation.screen.SearchAyah.SearchType
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

    @Serializable
    data object AzkarScreen

    @Serializable
    data class AzkarDetailScreen(val title: String) : Route

    @Serializable
    data object SurahListScreen

    @Serializable
    data class SurahAyatScreen(
        val surahId: Int,
        val surahName: String,
        val targetAyahId: Int? = null
    ) : Route

    @Serializable
    data class SearchAyahScreen(
        val type: SearchType,
        val surahId: Int? = null,
        val surahName: String? = null
    ) : Route

    @Serializable
    data object ReportBugScreen
}