package dev.sayed.mehrabalmomen.domain.di

import dev.sayed.mehrabalmomen.domain.usecase.GetPrayerTimelineUseCase
import dev.sayed.mehrabalmomen.domain.usecase.PrayerSchedulingUseCase
import org.koin.dsl.module

val domainModule = module {
    single { GetPrayerTimelineUseCase(get(), get()) }
    single { PrayerSchedulingUseCase(get(), get(), get(), get()) }
}
