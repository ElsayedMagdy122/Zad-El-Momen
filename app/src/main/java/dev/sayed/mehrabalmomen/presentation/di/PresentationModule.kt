package dev.sayed.mehrabalmomen.presentation.di

import com.google.firebase.analytics.FirebaseAnalytics
import dev.sayed.mehrabalmomen.presentation.screen.AzkarDetails.AzkarDetailViewModel
import dev.sayed.mehrabalmomen.presentation.screen.ReportBug.ReportBugViewModel
import dev.sayed.mehrabalmomen.presentation.screen.SearchAyah.SearchAyahViewModel
import dev.sayed.mehrabalmomen.presentation.screen.SurahAyat.SurahAyatViewModel
import dev.sayed.mehrabalmomen.presentation.screen.azkar.AzkarViewModel
import dev.sayed.mehrabalmomen.presentation.screen.batteryOptimization.BatteryOptimizationViewModel
import dev.sayed.mehrabalmomen.presentation.screen.bookmarks.BookMarkListViewModel
import dev.sayed.mehrabalmomen.presentation.screen.calculation_method.CalculationMethodViewModel
import dev.sayed.mehrabalmomen.presentation.screen.home.HomeViewModel
import dev.sayed.mehrabalmomen.presentation.screen.location_permission.LocationViewModel
import dev.sayed.mehrabalmomen.presentation.screen.madhab.MadhabViewModel
import dev.sayed.mehrabalmomen.presentation.screen.maps.MapsViewModel
import dev.sayed.mehrabalmomen.presentation.screen.prayers.FullPrayerTimesViewModel
import dev.sayed.mehrabalmomen.presentation.screen.qiblah.QiblahViewModel
import dev.sayed.mehrabalmomen.presentation.screen.quran.SurahListViewModel
import dev.sayed.mehrabalmomen.presentation.screen.radio.player.AudioPlayerManager
import dev.sayed.mehrabalmomen.presentation.screen.radio.player.AudioPlayerService
import dev.sayed.mehrabalmomen.presentation.screen.radio.player.PlayerController
import dev.sayed.mehrabalmomen.presentation.screen.radio.RadioChannelsViewModel
import dev.sayed.mehrabalmomen.presentation.screen.settings.SettingsViewModel
import dev.sayed.mehrabalmomen.presentation.utils.AnalyticsHelper
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val presentationModule = module {
    viewModelOf(::HomeViewModel)
    viewModelOf(::FullPrayerTimesViewModel)
    viewModelOf(::QiblahViewModel)
    viewModelOf(::LocationViewModel)
    viewModelOf(::MadhabViewModel)
    viewModelOf(::CalculationMethodViewModel)
    viewModelOf(::SettingsViewModel)
    viewModelOf(::MapsViewModel)
    viewModelOf(::AzkarViewModel)
    viewModelOf(::AzkarDetailViewModel)
    viewModelOf(::SurahListViewModel)
    viewModelOf(::SurahAyatViewModel)
    viewModelOf(::SearchAyahViewModel)
    viewModelOf(::ReportBugViewModel)
    viewModelOf(::BookMarkListViewModel)
    viewModelOf(::RadioChannelsViewModel)
    viewModelOf(::BatteryOptimizationViewModel)
    single{ AudioPlayerManager(get()) }
    single<PlayerController> { AudioPlayerManager(androidContext()) }
    single { AudioPlayerService() }
    single { FirebaseAnalytics.getInstance(get()) }
    single { AnalyticsHelper(get()) }
}