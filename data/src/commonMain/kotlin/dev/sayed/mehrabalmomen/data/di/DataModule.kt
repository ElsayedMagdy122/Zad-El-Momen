package dev.sayed.mehrabalmomen.data.di

import dev.sayed.mehrabalmomen.data.BuildKonfig
import dev.sayed.mehrabalmomen.data.azkar.repository.AzkarRepositoryImpl
import dev.sayed.mehrabalmomen.data.bugReport.remote.BugReportRpcService
import dev.sayed.mehrabalmomen.data.bugReport.remote.BugReportStorageService
import dev.sayed.mehrabalmomen.data.bugReport.repository.BugReportRepositoryImpl
import dev.sayed.mehrabalmomen.data.companion.CompanionRepositoryImpl
import dev.sayed.mehrabalmomen.data.prayer.repository.PrayerAlarmRepositoryImpl
import dev.sayed.mehrabalmomen.data.prayer.repository.PrayerNotificationsRepositoryImpl
import dev.sayed.mehrabalmomen.data.prayer.repository.PrayerRepositoryImpl
import dev.sayed.mehrabalmomen.data.qiblah.QiblahRepositoryImpl
import dev.sayed.mehrabalmomen.data.quran.audio.remote.QuranAudioReadersRemoteDataSource
import dev.sayed.mehrabalmomen.data.quran.audio.remote.QuranAudioReadersRemoteDataSourceImpl
import dev.sayed.mehrabalmomen.data.quran.audio.remote.QuranAudioRemoteDataSource
import dev.sayed.mehrabalmomen.data.quran.audio.remote.QuranAudioRemoteDataSourceImpl
import dev.sayed.mehrabalmomen.data.quran.audio.remote.QuranAudioTimingsRemoteDataSource
import dev.sayed.mehrabalmomen.data.quran.audio.remote.QuranAudioTimingsRemoteDataSourceImpl
import dev.sayed.mehrabalmomen.data.quran.audio.repository.QuranAudioRepositoryImpl
import dev.sayed.mehrabalmomen.data.quran.text.repository.BookmarkRepositoryImpl
import dev.sayed.mehrabalmomen.data.quran.text.repository.QuranRepositoryImpl
import dev.sayed.mehrabalmomen.data.quran.text.repository.ReadingProgressRepositoryImpl
import dev.sayed.mehrabalmomen.data.radio.repository.RadioRepositoryImpl
import dev.sayed.mehrabalmomen.data.reminders.ReminderSchedulerRepositoryImpl
import dev.sayed.mehrabalmomen.data.reminders.ReminderSettingsRepositoryImpl
import dev.sayed.mehrabalmomen.data.settings.local.RecitationPreferences
import dev.sayed.mehrabalmomen.data.settings.repositiory.SettingsRepositoryImpl
import dev.sayed.mehrabalmomen.data.util.AppDatabase
import dev.sayed.mehrabalmomen.domain.repository.azkar.AzkarRepository
import dev.sayed.mehrabalmomen.domain.repository.bugReport.BugReportRepository
import dev.sayed.mehrabalmomen.domain.repository.companion.CompanionRepository
import dev.sayed.mehrabalmomen.domain.repository.prayer.PrayerAlarmRepository
import dev.sayed.mehrabalmomen.domain.repository.prayer.PrayerNotificationsRepository
import dev.sayed.mehrabalmomen.domain.repository.prayer.PrayerRepository
import dev.sayed.mehrabalmomen.domain.repository.qiblah.QiblahRepository
import dev.sayed.mehrabalmomen.domain.repository.quran.BookmarkRepository
import dev.sayed.mehrabalmomen.domain.repository.quran.QuranAudioRepository
import dev.sayed.mehrabalmomen.domain.repository.quran.QuranRepository
import dev.sayed.mehrabalmomen.domain.repository.quran.ReadingProgressRepository
import dev.sayed.mehrabalmomen.domain.repository.radio.RadioRepository
import dev.sayed.mehrabalmomen.domain.repository.reminders.ReminderSchedulerRepository
import dev.sayed.mehrabalmomen.domain.repository.reminders.ReminderSettingsRepository
import dev.sayed.mehrabalmomen.domain.repository.settings.SettingsRepository
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.storage.Storage
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

val commonDataModule = module {
    single {
        createSupabaseClient(
            supabaseUrl = BuildKonfig.SUPABASE_URL,
            supabaseKey = BuildKonfig.SUPABASE_KEY,
        ) {
            install(Realtime)
            install(Postgrest)
            install(Storage)
        }
    }

    single {
        Json {
            ignoreUnknownKeys = true
            explicitNulls = false
            prettyPrint = false
        }
    }

    single {
        HttpClient {
            install(ContentNegotiation) {
                json(get())
            }
            install(HttpTimeout) {
                requestTimeoutMillis = 300_000
                connectTimeoutMillis = 300_000
                socketTimeoutMillis = 300_000
            }
        }
    }

    single { BugReportStorageService(get()) }
    single { BugReportRpcService(get()) }

    single<QuranAudioReadersRemoteDataSource> { QuranAudioReadersRemoteDataSourceImpl(get()) }
    single<QuranAudioRemoteDataSource> { QuranAudioRemoteDataSourceImpl(get()) }
    single<QuranAudioTimingsRemoteDataSource> { QuranAudioTimingsRemoteDataSourceImpl(get()) }

    single { RecitationPreferences(get(named("recitation_prefs"))) }

    single { get<AppDatabase>().bookmarkDao() }
    single { get<AppDatabase>().downloadedReciterDao() }
    single { get<AppDatabase>().azkarDao() }
    single { get<AppDatabase>().reminderDao() }

    single<PrayerRepository> { PrayerRepositoryImpl() }
    single<QiblahRepository> { QiblahRepositoryImpl(get()) }
    single<SettingsRepository> { SettingsRepositoryImpl(get()) }
    single<PrayerNotificationsRepository> { PrayerNotificationsRepositoryImpl(get()) }
    single<AzkarRepository> { AzkarRepositoryImpl(get()) }
    single<CompanionRepository> { CompanionRepositoryImpl(get()) }
    single<QuranRepository> { QuranRepositoryImpl(get(), get(), get()) }
    single<ReadingProgressRepository> { ReadingProgressRepositoryImpl(get()) }
    single<RadioRepository> { RadioRepositoryImpl(get()) }
    single<BookmarkRepository> { BookmarkRepositoryImpl(get()) }
    single<BugReportRepository> { BugReportRepositoryImpl(get()) }
    single<PrayerAlarmRepository> { PrayerAlarmRepositoryImpl(get()) }
    single<ReminderSettingsRepository> { ReminderSettingsRepositoryImpl(get()) }
    single<ReminderSchedulerRepository> { ReminderSchedulerRepositoryImpl(get(), get(), get(), get()) }
    single<QuranAudioRepository> { QuranAudioRepositoryImpl(get(), get(), get()) }
}

expect val platformDataModule: Module

val dataModule = listOf(commonDataModule, platformDataModule)
