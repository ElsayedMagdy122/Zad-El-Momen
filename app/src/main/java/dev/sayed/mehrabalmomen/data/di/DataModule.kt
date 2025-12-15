package dev.sayed.mehrabalmomen.data.di

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import dev.sayed.mehrabalmomen.data.LocationRepositoryImpl
import dev.sayed.mehrabalmomen.data.NetworkConnectionRepositoryImpl
import dev.sayed.mehrabalmomen.data.PrayerRepositoryImpl
import dev.sayed.mehrabalmomen.data.QiblahRepositoryImpl
import dev.sayed.mehrabalmomen.data.SettingsRepositoryImpl
import dev.sayed.mehrabalmomen.domain.repository.LocationRepository
import dev.sayed.mehrabalmomen.domain.repository.NetworkConnectionRepository
import dev.sayed.mehrabalmomen.domain.repository.PrayerRepository
import dev.sayed.mehrabalmomen.domain.repository.QiblahRepository
import dev.sayed.mehrabalmomen.domain.repository.SettingsRepository
import org.koin.dsl.module

private const val DATASTORE_NAME = "location_prefs"

val Context.locationDataStore by preferencesDataStore(name = DATASTORE_NAME)
val dataModule = module {
    single<PrayerRepository> { PrayerRepositoryImpl() }
    single<QiblahRepository> { QiblahRepositoryImpl() }
    single<SettingsRepository> { SettingsRepositoryImpl(get()) }
    single<NetworkConnectionRepository> { NetworkConnectionRepositoryImpl(get()) }
    single<LocationRepository> { LocationRepositoryImpl(get(), get(), get()) }
    single { get<Context>().locationDataStore }
}