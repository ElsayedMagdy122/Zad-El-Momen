package dev.sayed.mehrabalmomen.domain.repository.platform

/**
 * Interface for providing platform-specific device information.
 */
interface DeviceInfoProvider {
    /** Returns the device manufacturer and model (e.g., "Google Pixel 6"). */
    fun getDeviceModel(): String

    /** Returns the platform version (e.g., "Android 13"). */
    fun getOsVersion(): String
}
