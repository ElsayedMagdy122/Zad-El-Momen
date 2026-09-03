package dev.sayed.mehrabalmomen.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface Route {

    @Serializable
    data object AppRoute : Route

    @Serializable
    data object HomeScreen : Route

    @Serializable
    data object PrayerTimes : Route

    @Serializable
    data class RecitersSearchScreen(val surahId: Int, val currentReaderId: Int? = null) : Route
    
    @Serializable
    data object CalibrateDevice : Route

    @Serializable
    data object QiblahScreen : Route

    @Serializable
    data object PermissionsScreen : Route

    @Serializable
    data object MadhabScreen : Route

    @Serializable
    data object CalculationMethodScreen : Route

    @Serializable
    data object SettingsScreen : Route

    @Serializable
    data object MapsScreen : Route

    @Serializable
    data object AzkarScreen : Route

    @Serializable
    data class AzkarDetailScreen(val title: String) : Route

    @Serializable
    data object SurahListScreen : Route

    @Serializable
    data class SurahAyatScreen(
        val surahId: Int,
        val arabicName: String,
        val englishName: String,
        val targetAyahId: Int? = null
    ) : Route

    @Serializable
    data class RecitersScreen(val surahId: Int, val currentReaderId: Int? = null) : Route

    @Serializable
    data class SearchAyahScreen(
        val typeName: String, // Simplified for now to avoid dependency on SearchType if not moved
        val surahId: Int? = null,
        val surahName: String? = null
    ) : Route

    @Serializable
    data object ReportBugScreen : Route

    @Serializable
    data object BookmarksListScreen : Route

    @Serializable
    data object RadioScreen : Route

    @Serializable
    data object BatteryOptimizationScreen : Route

    @Serializable
    data object FAQScreen : Route

    @Serializable
    data object ContactUsScreen : Route

    @Serializable
    data object PrivacyScreen : Route

    @Serializable
    data object ReminderSettingsScreen : Route
}
