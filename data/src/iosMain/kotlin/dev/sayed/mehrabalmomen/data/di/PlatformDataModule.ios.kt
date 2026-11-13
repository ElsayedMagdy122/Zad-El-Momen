package dev.sayed.mehrabalmomen.data.di

import dev.sayed.mehrabalmomen.data.platform.IosAnalyticsTracker
import dev.sayed.mehrabalmomen.data.platform.IosAssetReader
import dev.sayed.mehrabalmomen.data.platform.IosAudioPlayer
import dev.sayed.mehrabalmomen.data.platform.IosBatteryOptimizationRepository
import dev.sayed.mehrabalmomen.data.platform.IosBillingService
import dev.sayed.mehrabalmomen.data.platform.IosCompassSensorManager
import dev.sayed.mehrabalmomen.data.platform.IosDeviceInfoProvider
import dev.sayed.mehrabalmomen.data.platform.IosImageCompressor
import dev.sayed.mehrabalmomen.data.platform.IosLocationRepository
import dev.sayed.mehrabalmomen.data.platform.IosLogger
import dev.sayed.mehrabalmomen.data.platform.IosNetworkConnectionRepository
import dev.sayed.mehrabalmomen.data.platform.IosPermissionProvider
import dev.sayed.mehrabalmomen.data.platform.IosReciterDownloader
import dev.sayed.mehrabalmomen.data.platform.sensor.CompassSensorManager
import dev.sayed.mehrabalmomen.data.platform.system.IosAlarmScheduler
import dev.sayed.mehrabalmomen.data.quran.audio.ReciterDownloader
import dev.sayed.mehrabalmomen.data.util.AssetReader
import dev.sayed.mehrabalmomen.data.util.helpers.ImageCompressor
import dev.sayed.mehrabalmomen.domain.analytics.AnalyticsTracker
import dev.sayed.mehrabalmomen.domain.billing.BillingService
import dev.sayed.mehrabalmomen.domain.repository.audio.AudioPlayer
import dev.sayed.mehrabalmomen.domain.repository.location.LocationRepository
import dev.sayed.mehrabalmomen.domain.repository.network.NetworkConnectionRepository
import dev.sayed.mehrabalmomen.domain.repository.platform.DeviceInfoProvider
import dev.sayed.mehrabalmomen.domain.repository.platform.PermissionProvider
import dev.sayed.mehrabalmomen.domain.repository.prayer.AlarmScheduler
import dev.sayed.mehrabalmomen.domain.repository.settings.BatteryOptimizationRepository
import dev.sayed.mehrabalmomen.domain.utils.Logger
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformDataModule: Module = module {
    single<AssetReader> { IosAssetReader() }
    single<ImageCompressor> { IosImageCompressor() }
    single<ReciterDownloader> { IosReciterDownloader() }
    single<BillingService> { IosBillingService() }
    single<CompassSensorManager> { IosCompassSensorManager() }
    factory<AudioPlayer> { IosAudioPlayer() }
    single<Logger> { IosLogger() }
    single<AlarmScheduler> { IosAlarmScheduler() }
    single<AnalyticsTracker> { IosAnalyticsTracker() }
    single<PermissionProvider> { IosPermissionProvider() }
    single<DeviceInfoProvider> { IosDeviceInfoProvider() }
    single<LocationRepository> { IosLocationRepository() }
    single<BatteryOptimizationRepository> { IosBatteryOptimizationRepository() }
    single<NetworkConnectionRepository> { IosNetworkConnectionRepository() }
}
