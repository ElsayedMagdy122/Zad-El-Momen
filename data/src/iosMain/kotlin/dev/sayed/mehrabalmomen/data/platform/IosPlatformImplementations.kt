package dev.sayed.mehrabalmomen.data.platform

import dev.sayed.mehrabalmomen.data.platform.sensor.CompassSensorManager
import dev.sayed.mehrabalmomen.data.quran.audio.ReciterDownloader
import dev.sayed.mehrabalmomen.data.util.AssetReader
import dev.sayed.mehrabalmomen.data.util.helpers.ImageCompressor
import dev.sayed.mehrabalmomen.domain.analytics.AnalyticsTracker
import dev.sayed.mehrabalmomen.domain.billing.BillingEvent
import dev.sayed.mehrabalmomen.domain.billing.BillingService
import dev.sayed.mehrabalmomen.domain.billing.DonationProduct
import dev.sayed.mehrabalmomen.domain.entity.location.Location
import dev.sayed.mehrabalmomen.domain.entity.quran.audio.QuranAudioReader
import dev.sayed.mehrabalmomen.domain.model.audio.AudioPlayerState
import dev.sayed.mehrabalmomen.domain.model.audio.AudioPlayerStatus
import dev.sayed.mehrabalmomen.domain.model.audio.AudioSource
import dev.sayed.mehrabalmomen.domain.model.audio.DownloadStatus
import dev.sayed.mehrabalmomen.domain.repository.audio.AudioPlayer
import dev.sayed.mehrabalmomen.domain.repository.location.LocationRepository
import dev.sayed.mehrabalmomen.domain.repository.network.NetworkConnectionRepository
import dev.sayed.mehrabalmomen.domain.repository.platform.DeviceInfoProvider
import dev.sayed.mehrabalmomen.domain.repository.platform.PermissionProvider
import dev.sayed.mehrabalmomen.domain.repository.settings.BatteryOptimizationRepository
import dev.sayed.mehrabalmomen.domain.utils.Logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow

class IosLogger : Logger {
    override fun d(tag: String, message: String) { println("DEBUG: [$tag] $message") }
    override fun e(tag: String, message: String, throwable: Throwable?) { println("ERROR: [$tag] $message") }
    override fun i(tag: String, message: String) { println("INFO: [$tag] $message") }
    override fun w(tag: String, message: String) { println("WARN: [$tag] $message") }
}

class IosAnalyticsTracker : AnalyticsTracker {
    override fun logEvent(name: String, params: Map<String, String>) {}
    override fun logScreen(screenName: String) {}
}

class IosPermissionProvider : PermissionProvider {
    override fun hasLocationPermission(): Boolean = true
    override fun hasNotificationPermission(): Boolean = true
    override fun canScheduleExactAlarms(): Boolean = true
    override fun isIgnoringBatteryOptimizations(): Boolean = true
    override fun canDrawOverlays(): Boolean = true
    override fun isNotificationPermissionRequired(): Boolean = true
    override fun isExactAlarmPermissionRequired(): Boolean = false
}

class IosDeviceInfoProvider : DeviceInfoProvider {
    override fun getDeviceModel(): String = "Apple Device"
    override fun getOsVersion(): String = "iOS"
    override fun getDeviceId(): String = "ios-simulator-device"
}

class IosLocationRepository : LocationRepository {
    override suspend fun getLocation(): Location = Location(latitude = 21.4225, longitude = 39.8262, country = "Saudi Arabia", state = "Mecca")
    override suspend fun getLocation(lat: Double, lng: Double): Location = Location(latitude = lat, longitude = lng, country = "Saudi Arabia", state = "Mecca")
    override suspend fun getCurrentDeviceLocation(): Location = Location(latitude = 21.4225, longitude = 39.8262, country = "Saudi Arabia", state = "Mecca")
}

class IosBatteryOptimizationRepository : BatteryOptimizationRepository {
    override fun getBrandInstructions(manufacturer: String, isArabic: Boolean): List<String> = emptyList()
    override fun isIgnoringBatteryOptimizations(): Boolean = true
}

class IosNetworkConnectionRepository : NetworkConnectionRepository {
    override fun isCurrentlyConnected(): Boolean = true
}

class IosCompassSensorManager : CompassSensorManager {
    override fun observeAzimuth(): Flow<Float> = emptyFlow()
}

class IosAssetReader : AssetReader {
    override suspend fun readText(fileName: String): String = "{}"
}

class IosImageCompressor : ImageCompressor {
    override suspend fun compressFromUri(uriString: String, maxWidth: Int, maxHeight: Int, quality: Int): ByteArray? = null
}

class IosReciterDownloader : ReciterDownloader {
    override fun downloadReciter(reciter: QuranAudioReader, surahId: Int) {}
    override fun observeDownloadStatus(reciterId: Int, surahId: Int): Flow<DownloadStatus> = emptyFlow()
}

class IosBillingService : BillingService {
    override val products: StateFlow<List<DonationProduct>> = MutableStateFlow(emptyList())
    override val events: SharedFlow<BillingEvent> = MutableSharedFlow()
    override val isBillingConnected: StateFlow<Boolean> = MutableStateFlow(false)
    override fun refresh() {}
    override fun donate(activity: Any, productId: String, quantity: Int) {}
}

class IosAudioPlayer : AudioPlayer {
    private val _state = MutableStateFlow(AudioPlayerState(status = AudioPlayerStatus.IDLE))
    override val playerState: StateFlow<AudioPlayerState> = _state
    override fun play(source: AudioSource, startPositionMs: Long) {}
    override fun pause() {}
    override fun resume() {}
    override fun stop() {}
    override fun seekTo(positionMs: Long) {}
    override fun setVolume(volume: Float) {}
    override fun release() {}
}
