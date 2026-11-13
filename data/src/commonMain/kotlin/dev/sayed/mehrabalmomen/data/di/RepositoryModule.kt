package dev.sayed.mehrabalmomen.data.di

import dev.sayed.mehrabalmomen.data.azkar.repository.AzkarRepositoryImpl
import dev.sayed.mehrabalmomen.data.bugReport.repository.BugReportRepositoryImpl
import dev.sayed.mehrabalmomen.data.companion.CompanionRepositoryImpl
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
import dev.sayed.mehrabalmomen.data.reminders.ReminderSchedulerRepositoryImpl
import dev.sayed.mehrabalmomen.data.reminders.ReminderSettingsRepositoryImpl
import dev.sayed.mehrabalmomen.data.settings.repositiory.SettingsRepositoryImpl
import dev.sayed.mehrabalmomen.domain.repository.azkar.AzkarRepository
import dev.sayed.mehrabalmomen.domain.repository.bugReport.BugReportRepository
import dev.sayed.mehrabalmomen.domain.repository.companion.CompanionRepository
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
import dev.sayed.mehrabalmomen.domain.repository.settings.SettingsRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<PrayerRepository> { PrayerRepositoryImpl() }
    single<QiblahRepository> { QiblahRepositoryImpl(get()) }
    single<SettingsRepository> { SettingsRepositoryImpl(get()) }
    single<PrayerNotificationsRepository> { PrayerNotificationsRepositoryImpl(get()) }
    single<AzkarRepository> { AzkarRepositoryImpl(get()) }
    single<CompanionRepository> { CompanionRepositoryImpl(get()) }
    single<QuranRepository> { QuranRepositoryImpl(get(), get(), get()) }
    single<ReadingProgressRepository> { ReadingProgressRepositoryImpl(get()) }
    single<RadioRepository> { RadioRepositoryImpl(get()) }
    single<BookmarkRepository> { BookmarkRepositoryImpl(dao = get()) }
    single<BugReportRepository> { BugReportRepositoryImpl(get()) }
    single<PrayerAlarmRepository> { PrayerAlarmRepositoryImpl(alarmScheduler = get()) }
    single<ReminderSettingsRepository> { ReminderSettingsRepositoryImpl(reminderDao = get()) }
    single<ReminderSchedulerRepository> {
        ReminderSchedulerRepositoryImpl(
            settingsRepository = get(),
            alarmScheduler = get(),
            prayerRepository = get(),
            appSettingsRepository = get()
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
            recitationPreferences = get(),
            reciterDownloader = get()
        )
    }
}
