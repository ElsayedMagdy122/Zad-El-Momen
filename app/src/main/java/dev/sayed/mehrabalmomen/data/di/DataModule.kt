package dev.sayed.mehrabalmomen.data.di

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.google.gson.Gson
import dev.sayed.mehrabalmomen.BuildConfig
import dev.sayed.mehrabalmomen.data.local.AzkarLocalDataSource
import dev.sayed.mehrabalmomen.data.local.quran.AppDatabase
import dev.sayed.mehrabalmomen.data.local.quran.repository.BookmarkRepositoryImpl
import dev.sayed.mehrabalmomen.data.local.quran.repository.QuranRepositoryImpl
import dev.sayed.mehrabalmomen.data.local.repository.AzkarRepositoryImpl
import dev.sayed.mehrabalmomen.data.local.repository.ReadingProgressRepositoryImpl
import dev.sayed.mehrabalmomen.data.local.repository.PrayerNotificationsRepositoryImpl
import dev.sayed.mehrabalmomen.data.local.repository.SettingsRepositoryImpl
import dev.sayed.mehrabalmomen.data.network.NetworkConnectionRepositoryImpl
import dev.sayed.mehrabalmomen.data.remote.BugReportRemoteDataSource
import dev.sayed.mehrabalmomen.data.remote.BugReportRemoteDataSourceImpl
import dev.sayed.mehrabalmomen.data.remote.BugReportRpcService
import dev.sayed.mehrabalmomen.data.remote.BugReportStorageService
import dev.sayed.mehrabalmomen.data.repository.BugReportRepositoryImpl
import dev.sayed.mehrabalmomen.data.repository.LocationRepositoryImpl
import dev.sayed.mehrabalmomen.data.repository.PrayerAlarmRepositoryImpl
import dev.sayed.mehrabalmomen.data.repository.PrayerRepositoryImpl
import dev.sayed.mehrabalmomen.data.repository.QiblahRepositoryImpl
import dev.sayed.mehrabalmomen.data.repository.RadioRepositoryImpl
import dev.sayed.mehrabalmomen.data.util.BillingManager
import dev.sayed.mehrabalmomen.domain.repository.AzkarRepository
import dev.sayed.mehrabalmomen.domain.repository.BookmarkRepository
import dev.sayed.mehrabalmomen.domain.repository.BugReportRepository
import dev.sayed.mehrabalmomen.domain.repository.ReadingProgressRepository
import dev.sayed.mehrabalmomen.domain.repository.LocationRepository
import dev.sayed.mehrabalmomen.domain.repository.NetworkConnectionRepository
import dev.sayed.mehrabalmomen.domain.repository.PrayerAlarmRepository
import dev.sayed.mehrabalmomen.domain.repository.PrayerNotificationsRepository
import dev.sayed.mehrabalmomen.domain.repository.PrayerRepository
import dev.sayed.mehrabalmomen.domain.repository.QiblahRepository
import dev.sayed.mehrabalmomen.domain.repository.QuranRepository
import dev.sayed.mehrabalmomen.domain.repository.RadioRepository
import dev.sayed.mehrabalmomen.domain.repository.SettingsRepository
import dev.sayed.mehrabalmomen.domain.usecase.PrayerSchedulingUseCase
import dev.sayed.mehrabalmomen.presentation.utils.AlarmScheduler
import io.github.jan.supabase.annotations.SupabaseInternal
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.storage.Storage
import io.ktor.client.plugins.HttpTimeout
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

private const val DATASTORE_NAME = "location_prefs"

val Context.locationDataStore by preferencesDataStore(name = DATASTORE_NAME)

@OptIn(SupabaseInternal::class)
val dataModule = module {
    // Context DataStore
    single { get<Context>().locationDataStore }
    single { Gson() }
    // Repositories
    single<PrayerRepository> { PrayerRepositoryImpl() }
    single<QiblahRepository> { QiblahRepositoryImpl() }
    single<SettingsRepository> { SettingsRepositoryImpl(get()) }
    single<PrayerNotificationsRepository> { PrayerNotificationsRepositoryImpl(get()) }
    single<NetworkConnectionRepository> { NetworkConnectionRepositoryImpl(get()) }
    single<LocationRepository> { LocationRepositoryImpl(get(), get()) }
    single<AzkarRepository> { AzkarRepositoryImpl(get()) }
    single<AzkarLocalDataSource> { AzkarLocalDataSource(get(), get()) }
    single<QuranRepository> { QuranRepositoryImpl(get(), get()) }
    single<ReadingProgressRepository> { ReadingProgressRepositoryImpl(get()) }
    single <RadioRepository>{ RadioRepositoryImpl(get()) }
    single<BookmarkRepository> {
        BookmarkRepositoryImpl(
            dao = get()
        )
    }
    // Scheduler dependencies
    single { AlarmScheduler(androidContext()) }

    // Scheduler repository
    single<PrayerAlarmRepository> {
        PrayerAlarmRepositoryImpl(
            context = androidContext(),
            alarmScheduler = get()
        )
    }
    // RPC
    single {
        BugReportRpcService(
            supabase = get()
        )
    }

    // Storage
    single {
        BugReportStorageService(
            supabase = get()
        )
    }

    // Remote DataSource
    single<BugReportRemoteDataSource> {
        BugReportRemoteDataSourceImpl(
            rpcService = get(),
            storageService = get(),
            supabase = get(),
            context = get()
        )
    }
    single {
        BillingManager(get())
    }
    single<BugReportRepository> {
        BugReportRepositoryImpl(
            get()
        )
    }
    single {
        val supabase = createSupabaseClient(
            supabaseKey = BuildConfig.SUPABASE_KEY,
            supabaseUrl = BuildConfig.SUPABASE_URL,
        ) {
            install(Realtime)
            install(Postgrest)
            install(Storage)
            httpConfig {
                install(HttpTimeout) {
                    requestTimeoutMillis = 10_000
                    connectTimeoutMillis = 15_000
                    socketTimeoutMillis = 15_000
                }
            }
        }
        supabase
    }

    single {
        Json {
            ignoreUnknownKeys = true
            explicitNulls = false
            prettyPrint = false
        }
    }
// Room Database
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "mehrab_database"
        ).build()
    }

// DAO
    single { get<AppDatabase>().bookmarkDao() }


    // Manager
    single { PrayerSchedulingUseCase(get(), get(), get(), get()) }
}