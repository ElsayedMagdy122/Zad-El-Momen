package dev.sayed.mehrabalmomen.data.di

import dev.sayed.mehrabalmomen.data.PrayerRepositoryImpl
import dev.sayed.mehrabalmomen.data.QiblaRepositoryImpl
import dev.sayed.mehrabalmomen.domain.repository.PrayerRepository
import dev.sayed.mehrabalmomen.domain.repository.QiblaRepository
import org.koin.dsl.module

val dataModule = module {
    single<PrayerRepository> { PrayerRepositoryImpl() }
    single<QiblaRepository> { QiblaRepositoryImpl() }
}