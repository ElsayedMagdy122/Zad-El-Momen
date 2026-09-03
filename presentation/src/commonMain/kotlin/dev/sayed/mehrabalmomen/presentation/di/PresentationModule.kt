package dev.sayed.mehrabalmomen.presentation.di

import dev.sayed.mehrabalmomen.domain.repository.audio.AudioPlayer
import dev.sayed.mehrabalmomen.presentation.screen.azkar.AzkarViewModel
import dev.sayed.mehrabalmomen.presentation.screen.home.HomeViewModel
import dev.sayed.mehrabalmomen.presentation.screen.onBoarding.batteryOptimization.BatteryOptimizationViewModel
import dev.sayed.mehrabalmomen.presentation.screen.onBoarding.calculation_method.CalculationMethodViewModel
import dev.sayed.mehrabalmomen.presentation.screen.onBoarding.permissions.PermissionsViewModel
import dev.sayed.mehrabalmomen.presentation.screen.companion.CompanionViewModel
import dev.sayed.mehrabalmomen.presentation.screen.prayers.PrayerTimesViewModel
import dev.sayed.mehrabalmomen.presentation.screen.radio.RadioChannelsViewModel
import dev.sayed.mehrabalmomen.presentation.screen.settings.SettingsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val presentationModule = module {
    viewModelOf(::HomeViewModel)
    viewModelOf(::PrayerTimesViewModel)
    viewModelOf(::PermissionsViewModel)
    viewModelOf(::CalculationMethodViewModel)
    viewModelOf(::SettingsViewModel)
    viewModelOf(::AzkarViewModel)
    viewModelOf(::RadioChannelsViewModel)
    viewModelOf(::BatteryOptimizationViewModel)
    viewModelOf(::CompanionViewModel)
    
    // Placeholder for other viewmodels as they are moved
    /*
    viewModelOf(::QiblahViewModel)
    viewModelOf(::MapsViewModel)
    viewModelOf(::AzkarDetailViewModel)
    viewModelOf(::SurahListViewModel)
    viewModelOf(::SurahAyatViewModel)
    viewModelOf(::SearchAyahViewModel)
    viewModelOf(::ReportBugViewModel)
    viewModelOf(::BookMarkListViewModel)
    viewModelOf(::ReminderSettingsViewModel)
    viewModelOf(::ContactViewModel)
    viewModelOf(::RecitersViewModel)
    viewModelOf(::RecitersSearchViewModel)
    */
    
    factory(named("quran")) { get<AudioPlayer>() }
    single(named("radio")) { get<AudioPlayer>() }
}
