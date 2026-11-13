package dev.sayed.mehrabalmomen.data.di

import android.content.Context
import androidx.media3.common.util.UnstableApi
import com.google.firebase.analytics.FirebaseAnalytics
import dev.sayed.mehrabalmomen.data.billing.BillingManager
import dev.sayed.mehrabalmomen.data.location.LocationRepositoryImpl
import dev.sayed.mehrabalmomen.data.platform.analytics.AndroidAnalyticsTracker
import dev.sayed.mehrabalmomen.data.platform.audio.AndroidAudioPlayer
import dev.sayed.mehrabalmomen.data.platform.sensor.AndroidCompassSensorManager
import dev.sayed.mehrabalmomen.data.platform.sensor.CompassSensorManager
import dev.sayed.mehrabalmomen.data.platform.system.AndroidDeviceInfoProvider
import dev.sayed.mehrabalmomen.data.platform.system.AndroidLogger
import dev.sayed.mehrabalmomen.data.platform.system.AndroidPermissionProvider
import dev.sayed.mehrabalmomen.data.quran.audio.AndroidReciterDownloader
import dev.sayed.mehrabalmomen.data.quran.audio.ReciterDownloader
import dev.sayed.mehrabalmomen.data.settings.repository.BatteryOptimizationRepositoryImpl
import dev.sayed.mehrabalmomen.data.util.AndroidAssetReader
import dev.sayed.mehrabalmomen.data.util.AssetReader
import dev.sayed.mehrabalmomen.data.util.helpers.AndroidImageCompressor
import dev.sayed.mehrabalmomen.data.util.helpers.ImageCompressor
import dev.sayed.mehrabalmomen.data.util.network.NetworkConnectionRepositoryImpl
import dev.sayed.mehrabalmomen.domain.analytics.AnalyticsTracker
import dev.sayed.mehrabalmomen.domain.billing.BillingService
import dev.sayed.mehrabalmomen.domain.repository.audio.AudioPlayer
import dev.sayed.mehrabalmomen.domain.repository.location.LocationRepository
import dev.sayed.mehrabalmomen.domain.repository.network.NetworkConnectionRepository
import dev.sayed.mehrabalmomen.domain.repository.platform.DeviceInfoProvider
import dev.sayed.mehrabalmomen.domain.repository.platform.PermissionProvider
import dev.sayed.mehrabalmomen.domain.repository.settings.BatteryOptimizationRepository
import dev.sayed.mehrabalmomen.domain.utils.Logger
import dev.sayed.mehrabalmomen.data.platform.system.AndroidAlarmScheduler
import dev.sayed.mehrabalmomen.domain.repository.prayer.AlarmScheduler
import org.koin.core.module.Module
import org.koin.dsl.module

@OptIn(UnstableApi::class)
actual val platformDataModule: Module = module {
    single<AssetReader> { AndroidAssetReader(get()) }
    single<ImageCompressor> { AndroidImageCompressor(get()) }
    single<ReciterDownloader> { AndroidReciterDownloader(get()) }
    single<BillingService> { BillingManager(get()) }
    single<CompassSensorManager> { AndroidCompassSensorManager(get()) }
    factory<AudioPlayer> { AndroidAudioPlayer(get()) }
    single<Logger> { AndroidLogger() }
    single<AlarmScheduler> { AndroidAlarmScheduler(get()) }
    single<AnalyticsTracker> { AndroidAnalyticsTracker(FirebaseAnalytics.getInstance(get<Context>())) }
    single<PermissionProvider> { AndroidPermissionProvider(get()) }
    single<DeviceInfoProvider> { AndroidDeviceInfoProvider(get()) }
    single<LocationRepository> { LocationRepositoryImpl(get(), get()) }
    single<BatteryOptimizationRepository> { BatteryOptimizationRepositoryImpl(get(), get()) }
    single<NetworkConnectionRepository> { NetworkConnectionRepositoryImpl(get()) }
}
