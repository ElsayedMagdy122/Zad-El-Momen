package dev.sayed.mehrabalmomen.domain.di

import dev.sayed.mehrabalmomen.domain.repository.quran.QuranPlaybackOrchestrator
import dev.sayed.mehrabalmomen.domain.usecase.PrayerSchedulingUseCase
import dev.sayed.mehrabalmomen.domain.usecase.ObserveCompanionUseCase
import org.koin.dsl.module

val domainModule = module {
    single { PrayerSchedulingUseCase(get(), get(), get(), get(), get()) }
    single { ObserveCompanionUseCase(get()) }
    single { QuranPlaybackOrchestrator() }
}
