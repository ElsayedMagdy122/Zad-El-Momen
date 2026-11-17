package dev.sayed.mehrabalmomen.data.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import dev.sayed.mehrabalmomen.data.bugReport.remote.BugReportRemoteDataSource
import dev.sayed.mehrabalmomen.data.bugReport.remote.IosBugReportRemoteDataSourceImpl
import dev.sayed.mehrabalmomen.data.database.getDatabaseBuilder
import dev.sayed.mehrabalmomen.data.datastore.createIosDataStore
import dev.sayed.mehrabalmomen.data.location.IosLocationRepositoryImpl
import dev.sayed.mehrabalmomen.data.platform.IosAlarmScheduler
import dev.sayed.mehrabalmomen.data.platform.IosNotificationScheduler
import dev.sayed.mehrabalmomen.data.platform.analytics.IosAnalyticsTracker
import dev.sayed.mehrabalmomen.data.platform.audio.IosAudioPlayer
import dev.sayed.mehrabalmomen.data.platform.sensor.IosCompassSensorManager
import dev.sayed.mehrabalmomen.data.platform.system.IosDeviceInfoProvider
import dev.sayed.mehrabalmomen.data.platform.system.IosPermissionProvider
import dev.sayed.mehrabalmomen.data.quran.audio.repository.IosQuranAudioReadersRepositoryImpl
import dev.sayed.mehrabalmomen.data.settings.repositiory.IosBatteryOptimizationRepositoryImpl
import dev.sayed.mehrabalmomen.data.util.AppDatabase
import dev.sayed.mehrabalmomen.data.util.IosBillingManager
import dev.sayed.mehrabalmomen.data.util.network.IosNetworkConnectionRepositoryImpl
import dev.sayed.mehrabalmomen.domain.analytics.AnalyticsTracker
import dev.sayed.mehrabalmomen.domain.billing.BillingService
import dev.sayed.mehrabalmomen.domain.repository.audio.AudioPlayer
import dev.sayed.mehrabalmomen.domain.repository.location.LocationRepository
import dev.sayed.mehrabalmomen.domain.repository.network.NetworkConnectionRepository
import dev.sayed.mehrabalmomen.domain.repository.notification.NotificationScheduler
import dev.sayed.mehrabalmomen.domain.repository.platform.DeviceInfoProvider
import dev.sayed.mehrabalmomen.domain.repository.platform.PermissionProvider
import dev.sayed.mehrabalmomen.domain.repository.prayer.AlarmScheduler
import dev.sayed.mehrabalmomen.domain.repository.qiblah.CompassSensorManager as DomainCompassSensorManager
import dev.sayed.mehrabalmomen.domain.repository.quran.QuranAudioReadersRepository
import dev.sayed.mehrabalmomen.domain.repository.settings.BatteryOptimizationRepository
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

actual val platformDataModule: Module = module {
    single<DataStore<Preferences>> { createIosDataStore("mehrab_settings.preferences_pb") }
    single<DataStore<Preferences>>(named("azkar_prefs")) { createIosDataStore("azkar_prefs.preferences_pb") }
    single<DataStore<Preferences>>(named("recitation_prefs")) { createIosDataStore("recitation_prefs.preferences_pb") }
    single<AppDatabase> {
        getDatabaseBuilder()
            .setDriver(BundledSQLiteDriver())
            .fallbackToDestructiveMigration(true)
            .build()
    }
    single<LocationRepository> { IosLocationRepositoryImpl(get()) }
    single<AlarmScheduler> { IosAlarmScheduler() }
    single<NotificationScheduler> { IosNotificationScheduler() }
    single<AnalyticsTracker> { IosAnalyticsTracker() }
    single<DeviceInfoProvider> { IosDeviceInfoProvider() }
    single<DomainCompassSensorManager> { IosCompassSensorManager() }
    single<BillingService> { IosBillingManager() }
    single<NetworkConnectionRepository> { IosNetworkConnectionRepositoryImpl() }
    single<BugReportRemoteDataSource> { IosBugReportRemoteDataSourceImpl() }
    single<BatteryOptimizationRepository> { IosBatteryOptimizationRepositoryImpl() }
    single<PermissionProvider> { IosPermissionProvider() }
    single<QuranAudioReadersRepository> {
        IosQuranAudioReadersRepositoryImpl(
            readersRemoteDataSource = get(),
            downloadedReciterDao = get(),
            recitationPreferences = get(),
            httpClient = get()
        )
    }
    factory<AudioPlayer> { IosAudioPlayer() }
    single<AudioPlayer>(named("radio")) { IosAudioPlayer() }
}
