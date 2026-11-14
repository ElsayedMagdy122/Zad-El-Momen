package dev.sayed.mehrabalmomen.data.platform

import dev.sayed.mehrabalmomen.domain.repository.platform.DeviceInfoProvider

class IosDeviceInfoProvider : DeviceInfoProvider {
    override fun getDeviceModel(): String = "Apple iPhone"
    override fun getOsVersion(): String = "iOS"
}
