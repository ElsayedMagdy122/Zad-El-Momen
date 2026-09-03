package dev.sayed.mehrabalmomen.domain.repository.platform

/**
 * Interface for providing platform-specific device information.
 */
interface DeviceInfoProvider {
    /** Returns the device manufacturer and model. */
    fun getDeviceModel(): String

    /** Returns the platform version. */
    fun getOsVersion(): String
}
