package dev.sayed.mehrabalmomen.data.di

import androidx.annotation.OptIn
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.media3.common.util.UnstableApi
import com.google.firebase.analytics.FirebaseAnalytics
import dev.sayed.mehrabalmomen.data.platform.system.AndroidImageCompressor
import dev.sayed.mehrabalmomen.domain.repository.platform.ImageCompressor
import dev.sayed.mehrabalmomen.domain.repository.platform.WidgetSync
import dev.sayed.mehrabalmomen.data.platform.AndroidWidgetSync
import dev.sayed.mehrabalmomen.data.database.getDatabaseBuilder
import dev.sayed.mehrabalmomen.data.database.roomDatabaseCallback
import dev.sayed.mehrabalmomen.data.datastore.createAndroidDataStore
import dev.sayed.mehrabalmomen.data.location.LocationRepositoryImpl
import dev.sayed.mehrabalmomen.data.platform.AndroidAlarmScheduler
import dev.sayed.mehrabalmomen.data.platform.AndroidNotificationScheduler
import dev.sayed.mehrabalmomen.data.platform.analytics.AndroidAnalyticsTracker
import dev.sayed.mehrabalmomen.data.platform.audio.AndroidAudioPlayer
import dev.sayed.mehrabalmomen.data.platform.sensor.CompassSensorManager
import dev.sayed.mehrabalmomen.data.platform.system.AndroidDeviceInfoProvider
import dev.sayed.mehrabalmomen.data.platform.system.AndroidPermissionProvider
import dev.sayed.mehrabalmomen.data.quran.audio.repository.QuranAudioReadersRepositoryImpl
import dev.sayed.mehrabalmomen.data.recitation.audio.AudioPipeline
import dev.sayed.mehrabalmomen.data.recitation.engine.RecitationModelDownloader
import dev.sayed.mehrabalmomen.data.recitation.engine.SherpaEngine
import dev.sayed.mehrabalmomen.data.settings.repositiory.BatteryOptimizationRepositoryImpl
import dev.sayed.mehrabalmomen.data.util.AppDatabase
import dev.sayed.mehrabalmomen.data.util.BillingManager
import dev.sayed.mehrabalmomen.data.util.network.NetworkConnectionRepositoryImpl
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
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

@OptIn(UnstableApi::class)
actual val platformDataModule: Module = module {
    single<DataStore<Preferences>> { createAndroidDataStore(androidContext(), "mehrab_settings.preferences_pb") }
    single<DataStore<Preferences>>(named("azkar_prefs")) { createAndroidDataStore(androidContext(), "azkar_prefs.preferences_pb") }
    single<DataStore<Preferences>>(named("recitation_prefs")) { createAndroidDataStore(androidContext(), "recitation_prefs.preferences_pb") }
    single<AppDatabase> {
        getDatabaseBuilder()
            .addCallback(roomDatabaseCallback)
            .fallbackToDestructiveMigration(true)
            .build()
    }
    single<DomainCompassSensorManager> { CompassSensorManager(androidContext()) }
    single<AlarmScheduler> { AndroidAlarmScheduler(androidContext()) }
    single<NotificationScheduler> { AndroidNotificationScheduler(androidContext()) }
    factory<AudioPlayer> { AndroidAudioPlayer(androidContext()) }
    single<AudioPlayer>(named("radio")) { AndroidAudioPlayer(androidContext()) }
    @Suppress("MissingPermission")
    single<AnalyticsTracker> { AndroidAnalyticsTracker(FirebaseAnalytics.getInstance(androidContext())) }
    single<PermissionProvider> { AndroidPermissionProvider(androidContext()) }
    single<DeviceInfoProvider> { AndroidDeviceInfoProvider(androidContext()) }
    single<ImageCompressor> { AndroidImageCompressor(androidContext()) }
    single<BillingService> { BillingManager(androidContext()) }
    single<BatteryOptimizationRepository> { BatteryOptimizationRepositoryImpl(androidContext(), get()) }

    single<NetworkConnectionRepository> { NetworkConnectionRepositoryImpl(androidContext()) }
    single<LocationRepository> { LocationRepositoryImpl(androidContext(), get()) }
    single<QuranAudioReadersRepository> {
        QuranAudioReadersRepositoryImpl(
            readersRemoteDataSource = get(),
            downloadedReciterDao = get(),
            recitationPreferences = get(),
            context = androidContext()
        )
    }
    single {
        RecitationModelDownloader(
            context = androidContext(),
            supabase = get(),
            httpClient = get()
        )
    }
    single { AudioPipeline() }
    single { SherpaEngine(androidContext()) }
    single<WidgetSync> { AndroidWidgetSync() }
}
