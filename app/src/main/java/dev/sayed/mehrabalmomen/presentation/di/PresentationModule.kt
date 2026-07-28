package dev.sayed.mehrabalmomen.presentation.di

import com.google.firebase.analytics.FirebaseAnalytics
import dev.sayed.mehrabalmomen.presentation.screen.reminders.ReminderSettingsViewModel
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
import dev.sayed.mehrabalmomen.presentation.screen.radio.player.PlayerController
import dev.sayed.mehrabalmomen.presentation.screen.radio.RadioChannelsViewModel
import dev.sayed.mehrabalmomen.presentation.screen.settings.SettingsViewModel
import dev.sayed.mehrabalmomen.presentation.screen.settings.contact_us.ContactViewModel
import dev.sayed.mehrabalmomen.presentation.utils.AnalyticsHelper
import dev.sayed.mehrabalmomen.presentation.widget.prayer.AndroidPrayerWidgetBoundaryAlarm
import dev.sayed.mehrabalmomen.presentation.widget.prayer.PrayerWidgetBoundaryAlarm
import dev.sayed.mehrabalmomen.presentation.widget.prayer.PrayerWidgetBoundaryScheduler
import dev.sayed.mehrabalmomen.presentation.widget.prayer.PrayerWidgetProgressScheduler
import dev.sayed.mehrabalmomen.presentation.widget.prayer.PrayerWidgetSettingsRefreshObserver
import dev.sayed.mehrabalmomen.presentation.widget.prayer.PrayerWidgetUpdateCoordinator
import dev.sayed.mehrabalmomen.presentation.widget.prayer.mapper.PrayerWidgetSnapshotMapper
import dev.sayed.mehrabalmomen.domain.repository.settings.SettingsRepository
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
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
    viewModelOf(:: ReminderSettingsViewModel)
    viewModelOf(::ContactViewModel)
    single<PlayerController> { AudioPlayerManager(androidContext()) }
    single { FirebaseAnalytics.getInstance(get()) }
    single { AnalyticsHelper(get()) }
    single { PrayerWidgetSnapshotMapper() }
    single<PrayerWidgetBoundaryAlarm> { AndroidPrayerWidgetBoundaryAlarm(androidContext()) }
    single { PrayerWidgetBoundaryScheduler(get(), get()) }
    single { PrayerWidgetProgressScheduler.from(androidContext()) }
    single { PrayerWidgetUpdateCoordinator(androidContext(), get(), get()) }
    single {
        val coordinator: PrayerWidgetUpdateCoordinator = get()
        PrayerWidgetSettingsRefreshObserver(
            get<SettingsRepository>().observePrayerWidgetSettings(),
            coordinator::refreshAllIfInstalled,
        )
    }
}
