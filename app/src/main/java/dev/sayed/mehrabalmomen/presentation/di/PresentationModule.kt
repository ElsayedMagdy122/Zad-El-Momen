package dev.sayed.mehrabalmomen.presentation.di

import com.google.firebase.analytics.FirebaseAnalytics
import dev.sayed.mehrabalmomen.data.audio.ExoAudioPlayerManager
import dev.sayed.mehrabalmomen.presentation.screen.AzkarDetails.AzkarDetailViewModel
import dev.sayed.mehrabalmomen.presentation.screen.ReportBug.ReportBugViewModel
import dev.sayed.mehrabalmomen.presentation.screen.SearchAyah.SearchAyahViewModel
import dev.sayed.mehrabalmomen.presentation.screen.azkar.AzkarViewModel
import dev.sayed.mehrabalmomen.presentation.screen.bookmarks.BookMarkListViewModel
import dev.sayed.mehrabalmomen.presentation.screen.home.HomeViewModel
import dev.sayed.mehrabalmomen.presentation.screen.maps.MapsViewModel
import dev.sayed.mehrabalmomen.presentation.screen.onBoarding.batteryOptimization.BatteryOptimizationViewModel
import dev.sayed.mehrabalmomen.presentation.screen.onBoarding.calculation_method.CalculationMethodViewModel
import dev.sayed.mehrabalmomen.presentation.screen.onBoarding.permissions.PermissionsViewModel
import dev.sayed.mehrabalmomen.presentation.screen.prayers.FullPrayerTimesViewModel
import dev.sayed.mehrabalmomen.presentation.screen.qiblah.QiblahViewModel
import dev.sayed.mehrabalmomen.presentation.screen.quran.SurahAyat.SurahAyatViewModel
import dev.sayed.mehrabalmomen.presentation.screen.quran.SurahList.SurahListViewModel
import dev.sayed.mehrabalmomen.presentation.screen.quran.audio_utils.AudioPlayerManager
import dev.sayed.mehrabalmomen.presentation.screen.quran.reciters.RecitersViewModel
import dev.sayed.mehrabalmomen.presentation.screen.quran.reciters_search.RecitersSearchViewModel
import dev.sayed.mehrabalmomen.presentation.screen.radio.RadioChannelsViewModel
import dev.sayed.mehrabalmomen.presentation.screen.radio.player.PlayerController
import dev.sayed.mehrabalmomen.presentation.screen.radio.player.RadioAudioPlayerManager
import dev.sayed.mehrabalmomen.presentation.screen.reminders.ReminderSettingsViewModel
import dev.sayed.mehrabalmomen.presentation.screen.settings.SettingsViewModel
import dev.sayed.mehrabalmomen.presentation.screen.settings.contact_us.ContactViewModel
import dev.sayed.mehrabalmomen.presentation.utils.AnalyticsHelper
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val presentationModule = module {
    viewModelOf(::HomeViewModel)
    viewModelOf(::FullPrayerTimesViewModel)
    viewModelOf(::QiblahViewModel)
    viewModelOf(::PermissionsViewModel)
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
    viewModelOf(::ReminderSettingsViewModel)
    viewModelOf(::ContactViewModel)
    viewModelOf(::RecitersViewModel)
    viewModelOf(::RecitersSearchViewModel)
    factory<AudioPlayerManager> { ExoAudioPlayerManager(androidContext()) }
    single<PlayerController> { RadioAudioPlayerManager(androidContext()) }
    single { FirebaseAnalytics.getInstance(get()) }
    single { AnalyticsHelper(get()) }
}