package dev.sayed.mehrabalmomen.presentation.navigation

import dev.sayed.mehrabalmomen.presentation.screen.SearchAyah.SearchType
import kotlinx.serialization.Serializable


@Serializable
sealed interface Route {
    val route: String

    @Serializable
    data object AppRoute : Route { override val route = "app_route" }

    @Serializable
    data object HomeScreen : Route { override val route = "home_screen" }

    @Serializable
    data object FullPrayerTimeView : Route { override val route = "full_prayer" }

    @Serializable
    data object CalibrateDevice : Route { override val route = "calibrate_device" }

    @Serializable
    data object QiblahScreen : Route { override val route = "qiblah_screen" }

    @Serializable
    data object LocationPermissionScreen : Route { override val route = "location_permission" }

    @Serializable
    data object MadhabScreen : Route { override val route = "madhab_screen" }

    @Serializable
    data object CalculationMethodScreen : Route { override val route = "calculation_method" }

    @Serializable
    data object SettingsScreen : Route { override val route = "settings_screen" }

    @Serializable
    data object MapsScreen : Route { override val route = "maps_screen" }

    @Serializable
    data object AzkarScreen : Route { override val route = "azkar_screen" }

    @Serializable
    data class AzkarDetailScreen(val title: String) : Route {
        override val route: String get() = "azkar_detail_screen/$title"
    }

    @Serializable
    data object SurahListScreen : Route { override val route = "surah_list_screen" }

    @Serializable
    data class SurahAyatScreen(
        val surahId: Int,
        val arabicName: String,
        val englishName: String,
        val targetAyahId: Int? = null
    ) : Route {
        override val route: String get() = "surah_ayat_screen/$surahId"
    }

    @Serializable
    data class SearchAyahScreen(
        val type: SearchType,
        val surahId: Int? = null,
        val surahName: String? = null
    ) : Route {
        override val route: String get() = "search_ayah_screen/${type.name}"
    }

    @Serializable
    data object ReportBugScreen : Route { override val route = "report_bug" }

    @Serializable
    data object BookmarksListScreen : Route { override val route = "bookmarks_list" }

    @Serializable
    data object RadioScreen : Route { override val route = "radio_screen" }

    @Serializable
    data object BatteryOptimizationScreen : Route { override val route = "battery_optimization" }
}