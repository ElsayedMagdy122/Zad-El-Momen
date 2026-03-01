package dev.sayed.mehrabalmomen.data.di

import dev.sayed.mehrabalmomen.data.local.quran.repository.BookmarkRepositoryImpl
import dev.sayed.mehrabalmomen.data.local.quran.repository.QuranRepositoryImpl
import dev.sayed.mehrabalmomen.data.local.repository.AzkarRepositoryImpl
import dev.sayed.mehrabalmomen.data.local.repository.PrayerNotificationsRepositoryImpl
import dev.sayed.mehrabalmomen.data.local.repository.ReadingProgressRepositoryImpl
import dev.sayed.mehrabalmomen.data.local.repository.SettingsRepositoryImpl
import dev.sayed.mehrabalmomen.data.network.NetworkConnectionRepositoryImpl
import dev.sayed.mehrabalmomen.data.repository.BugReportRepositoryImpl
import dev.sayed.mehrabalmomen.data.repository.LocationRepositoryImpl
import dev.sayed.mehrabalmomen.data.repository.PrayerAlarmRepositoryImpl
import dev.sayed.mehrabalmomen.data.repository.PrayerRepositoryImpl
import dev.sayed.mehrabalmomen.data.repository.QiblahRepositoryImpl
import dev.sayed.mehrabalmomen.data.repository.RadioRepositoryImpl
import dev.sayed.mehrabalmomen.domain.repository.azkar.AzkarRepository
import dev.sayed.mehrabalmomen.domain.repository.bugReport.BugReportRepository
import dev.sayed.mehrabalmomen.domain.repository.location.LocationRepository
import dev.sayed.mehrabalmomen.domain.repository.network.NetworkConnectionRepository
import dev.sayed.mehrabalmomen.domain.repository.prayer.PrayerAlarmRepository
import dev.sayed.mehrabalmomen.domain.repository.prayer.PrayerNotificationsRepository
import dev.sayed.mehrabalmomen.domain.repository.prayer.PrayerRepository
import dev.sayed.mehrabalmomen.domain.repository.qiblah.QiblahRepository
import dev.sayed.mehrabalmomen.domain.repository.quran.BookmarkRepository
import dev.sayed.mehrabalmomen.domain.repository.quran.QuranRepository
import dev.sayed.mehrabalmomen.domain.repository.quran.ReadingProgressRepository
import dev.sayed.mehrabalmomen.domain.repository.radio.RadioRepository
import dev.sayed.mehrabalmomen.domain.repository.settings.SettingsRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repositoryModule = module {
    single<PrayerRepository> { PrayerRepositoryImpl() }
    single<QiblahRepository> { QiblahRepositoryImpl() }
    single<SettingsRepository> { SettingsRepositoryImpl(get()) }
    single<PrayerNotificationsRepository> { PrayerNotificationsRepositoryImpl(get()) }
    single<NetworkConnectionRepository> { NetworkConnectionRepositoryImpl(get()) }
    single<LocationRepository> { LocationRepositoryImpl(get(), get()) }
    single<AzkarRepository> { AzkarRepositoryImpl(get()) }

    single<QuranRepository> { QuranRepositoryImpl(get(), get()) }
    single<ReadingProgressRepository> { ReadingProgressRepositoryImpl(get()) }
    single <RadioRepository>{ RadioRepositoryImpl(get()) }
    single<BookmarkRepository> {
        BookmarkRepositoryImpl(
            dao = get()
        )
    }
    single<BugReportRepository> {
        BugReportRepositoryImpl(
            get()
        )
    }
    single<PrayerAlarmRepository> {
        PrayerAlarmRepositoryImpl(
            context = androidContext(),
            alarmScheduler = get()
        )
    }
}