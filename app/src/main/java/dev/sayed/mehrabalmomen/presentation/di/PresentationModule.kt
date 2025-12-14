package dev.sayed.mehrabalmomen.presentation.di

import dev.sayed.mehrabalmomen.presentation.screen.calculation_method.CalculationMethodViewModel
import dev.sayed.mehrabalmomen.presentation.screen.home.HomeViewModel
import dev.sayed.mehrabalmomen.presentation.screen.location_permission.LocationViewModel
import dev.sayed.mehrabalmomen.presentation.screen.madhab.MadhabViewModel
import dev.sayed.mehrabalmomen.presentation.screen.prayers.FullPrayerTimesViewModel
import dev.sayed.mehrabalmomen.presentation.screen.qiblah.QiblahViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val presentationModule = module {
    viewModelOf(::HomeViewModel)
    viewModelOf(::FullPrayerTimesViewModel)
    viewModelOf(::QiblahViewModel)
    viewModelOf(::LocationViewModel)
    viewModelOf(::MadhabViewModel)
    viewModelOf(::CalculationMethodViewModel)
}