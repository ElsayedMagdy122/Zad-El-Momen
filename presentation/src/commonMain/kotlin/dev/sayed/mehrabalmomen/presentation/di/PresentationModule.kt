package dev.sayed.mehrabalmomen.presentation.di

import dev.sayed.mehrabalmomen.presentation.screen.home.HomeViewModel
import dev.sayed.mehrabalmomen.presentation.screen.onBoarding.calculation_method.CalculationMethodViewModel
import dev.sayed.mehrabalmomen.presentation.screen.onBoarding.permissions.PermissionsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val presentationModule = module {
    viewModelOf(::HomeViewModel)
    viewModelOf(::CalculationMethodViewModel)
    viewModelOf(::PermissionsViewModel)
}
