package dev.sayed.mehrabalmomen.data.di

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import dev.sayed.mehrabalmomen.data.LocationRepositoryImpl
import dev.sayed.mehrabalmomen.data.PrayerRepositoryImpl
import dev.sayed.mehrabalmomen.data.QiblahRepositoryImpl
import dev.sayed.mehrabalmomen.domain.repository.LocationRepository
import dev.sayed.mehrabalmomen.domain.repository.PrayerRepository
import dev.sayed.mehrabalmomen.domain.repository.QiblahRepository
import org.koin.dsl.module

private const val DATASTORE_NAME = "location_prefs"

val Context.locationDataStore by preferencesDataStore(name = DATASTORE_NAME)
val dataModule = module {
    single<PrayerRepository> { PrayerRepositoryImpl() }
    single<QiblahRepository> { QiblahRepositoryImpl() }
    single<LocationRepository> { LocationRepositoryImpl(get(),get()) }
    single { get<Context>().locationDataStore }
}