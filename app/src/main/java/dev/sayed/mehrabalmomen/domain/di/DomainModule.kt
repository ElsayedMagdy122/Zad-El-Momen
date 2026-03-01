package dev.sayed.mehrabalmomen.domain.di

import dev.sayed.mehrabalmomen.domain.usecase.PrayerSchedulingUseCase
import org.koin.dsl.module

val domainModule = module {
    single { PrayerSchedulingUseCase(get(), get(), get(), get()) }
}