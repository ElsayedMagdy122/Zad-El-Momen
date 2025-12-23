package dev.sayed.mehrabalmomen.data.di

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import dev.sayed.mehrabalmomen.data.AlarmScheduler
import dev.sayed.mehrabalmomen.data.AzanManager
import dev.sayed.mehrabalmomen.data.ExactAlarmPermissionDataSource
import dev.sayed.mehrabalmomen.data.repository.AzanSchedulerRepositoryImpl
import dev.sayed.mehrabalmomen.data.repository.LocationRepositoryImpl
import dev.sayed.mehrabalmomen.data.repository.NetworkConnectionRepositoryImpl
import dev.sayed.mehrabalmomen.data.repository.PrayerNotificationsRepositoryImpl
import dev.sayed.mehrabalmomen.data.repository.PrayerRepositoryImpl
import dev.sayed.mehrabalmomen.data.repository.QiblahRepositoryImpl
import dev.sayed.mehrabalmomen.data.repository.SettingsRepositoryImpl
import dev.sayed.mehrabalmomen.domain.repository.AzanSchedulerRepository
import dev.sayed.mehrabalmomen.domain.repository.LocationRepository
import dev.sayed.mehrabalmomen.domain.repository.NetworkConnectionRepository
import dev.sayed.mehrabalmomen.domain.repository.PrayerNotificationsRepository
import dev.sayed.mehrabalmomen.domain.repository.PrayerRepository
import dev.sayed.mehrabalmomen.domain.repository.QiblahRepository
import dev.sayed.mehrabalmomen.domain.repository.SettingsRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

private const val DATASTORE_NAME = "location_prefs"

val Context.locationDataStore by preferencesDataStore(name = DATASTORE_NAME)
val dataModule = module {
    // Context DataStore
    single { get<Context>().locationDataStore }

    // Repositories
    single<PrayerRepository> { PrayerRepositoryImpl() }
    single<QiblahRepository> { QiblahRepositoryImpl() }
    single<SettingsRepository> { SettingsRepositoryImpl(get()) }
    single<PrayerNotificationsRepository> { PrayerNotificationsRepositoryImpl(get()) }
    single<NetworkConnectionRepository> { NetworkConnectionRepositoryImpl(get()) }
    single<LocationRepository> { LocationRepositoryImpl(get(), get(), get()) }

    // Scheduler dependencies
    single { ExactAlarmPermissionDataSource(androidContext()) }
    single { AlarmScheduler(androidContext()) }

    // Scheduler repository
    single<AzanSchedulerRepository> {
        AzanSchedulerRepositoryImpl(
            context = androidContext(),
            alarmScheduler = get(),
            permission = get()
        )
    }

    // Manager
    single { AzanManager(get(), get(), get(),get()) }
}