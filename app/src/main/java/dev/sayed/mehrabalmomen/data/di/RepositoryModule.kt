package dev.sayed.mehrabalmomen.data.di

import dev.sayed.mehrabalmomen.data.azkar.repository.AzkarRepositoryImpl
import dev.sayed.mehrabalmomen.data.bugReport.repository.BugReportRepositoryImpl
import dev.sayed.mehrabalmomen.data.location.LocationRepositoryImpl
import dev.sayed.mehrabalmomen.data.prayer.repository.PrayerAlarmRepositoryImpl
import dev.sayed.mehrabalmomen.data.prayer.repository.PrayerNotificationsRepositoryImpl
import dev.sayed.mehrabalmomen.data.prayer.repository.PrayerRepositoryImpl
import dev.sayed.mehrabalmomen.data.qiblah.QiblahRepositoryImpl
import dev.sayed.mehrabalmomen.data.quran.audio.repository.QuranAudioReadersRepositoryImpl
import dev.sayed.mehrabalmomen.data.quran.audio.repository.QuranAudioRepositoryImpl
import dev.sayed.mehrabalmomen.data.quran.text.repository.BookmarkRepositoryImpl
import dev.sayed.mehrabalmomen.data.quran.text.repository.QuranRepositoryImpl
import dev.sayed.mehrabalmomen.data.quran.text.repository.ReadingProgressRepositoryImpl
import dev.sayed.mehrabalmomen.data.radio.repository.RadioRepositoryImpl
import dev.sayed.mehrabalmomen.data.repository.TimeRepositoryImpl
import dev.sayed.mehrabalmomen.data.reminders.ReminderSchedulerRepositoryImpl
import dev.sayed.mehrabalmomen.data.reminders.ReminderSettingsRepositoryImpl
import dev.sayed.mehrabalmomen.data.settings.repositiory.BatteryOptimizationRepositoryImpl
import dev.sayed.mehrabalmomen.data.settings.repositiory.SettingsRepositoryImpl
import dev.sayed.mehrabalmomen.data.util.network.NetworkConnectionRepositoryImpl
import dev.sayed.mehrabalmomen.data.widget.repository.ExactAlarmPermissionRepositoryImpl
import dev.sayed.mehrabalmomen.domain.repository.azkar.AzkarRepository
import dev.sayed.mehrabalmomen.domain.repository.bugReport.BugReportRepository
import dev.sayed.mehrabalmomen.domain.repository.location.LocationRepository
import dev.sayed.mehrabalmomen.domain.repository.network.NetworkConnectionRepository
import dev.sayed.mehrabalmomen.domain.repository.prayer.PrayerAlarmRepository
import dev.sayed.mehrabalmomen.domain.repository.prayer.PrayerNotificationsRepository
import dev.sayed.mehrabalmomen.domain.repository.prayer.PrayerRepository
import dev.sayed.mehrabalmomen.domain.repository.qiblah.QiblahRepository
import dev.sayed.mehrabalmomen.domain.repository.quran.BookmarkRepository
import dev.sayed.mehrabalmomen.domain.repository.quran.QuranAudioReadersRepository
import dev.sayed.mehrabalmomen.domain.repository.quran.QuranAudioRepository
import dev.sayed.mehrabalmomen.domain.repository.quran.QuranRepository
import dev.sayed.mehrabalmomen.domain.repository.quran.ReadingProgressRepository
import dev.sayed.mehrabalmomen.domain.repository.radio.RadioRepository
import dev.sayed.mehrabalmomen.domain.repository.reminders.ReminderSchedulerRepository
import dev.sayed.mehrabalmomen.domain.repository.reminders.ReminderSettingsRepository
import dev.sayed.mehrabalmomen.domain.repository.settings.BatteryOptimizationRepository
import dev.sayed.mehrabalmomen.domain.repository.settings.SettingsRepository
import dev.sayed.mehrabalmomen.domain.repository.TimeRepository
import dev.sayed.mehrabalmomen.domain.repository.widget.ExactAlarmPermissionRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repositoryModule = module {
    single<PrayerRepository> { PrayerRepositoryImpl() }
    single<TimeRepository> { TimeRepositoryImpl() }
    single<ExactAlarmPermissionRepository> { ExactAlarmPermissionRepositoryImpl(androidContext()) }
    single<QiblahRepository> { QiblahRepositoryImpl() }
    single<SettingsRepository> { SettingsRepositoryImpl(get()) }
    single<PrayerNotificationsRepository> { PrayerNotificationsRepositoryImpl(get()) }
    single<NetworkConnectionRepository> { NetworkConnectionRepositoryImpl(get()) }
    single<LocationRepository> { LocationRepositoryImpl(get(), get()) }
    single<AzkarRepository> { AzkarRepositoryImpl(get()) }
    single<BatteryOptimizationRepository> { BatteryOptimizationRepositoryImpl(get(), get()) }
    single<QuranRepository> { QuranRepositoryImpl(get(), get(), get()) }
    single<ReadingProgressRepository> { ReadingProgressRepositoryImpl(get()) }
    single<RadioRepository> { RadioRepositoryImpl(get()) }
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

    single<ReminderSettingsRepository> {
        ReminderSettingsRepositoryImpl(dataStore = get())
    }

    single<ReminderSchedulerRepository> {
        ReminderSchedulerRepositoryImpl(
            context = androidContext(),
            settingsRepository = get(),
            alarmScheduler = get()
        )
    }
    single<QuranAudioRepository> { 
        QuranAudioRepositoryImpl(
            readersRemoteDataSource = get(),
            timingsRemoteDataSource = get(),
            downloadedReciterDao = get()
        ) 
    }
    single<QuranAudioReadersRepository> {
        QuranAudioReadersRepositoryImpl(
            readersRemoteDataSource = get(),
            downloadedReciterDao = get(),
            context = androidContext()
        )
    }
}
