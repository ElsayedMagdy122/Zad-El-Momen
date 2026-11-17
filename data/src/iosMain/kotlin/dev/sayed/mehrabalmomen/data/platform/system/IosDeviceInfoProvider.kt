package dev.sayed.mehrabalmomen.data.platform.system

import dev.sayed.mehrabalmomen.domain.repository.platform.DeviceInfoProvider
import platform.UIKit.UIDevice

class IosDeviceInfoProvider : DeviceInfoProvider {
    override fun getDeviceModel(): String {
        return "Apple ${UIDevice.currentDevice.model}"
    }

    override fun getOsVersion(): String {
        return "iOS ${UIDevice.currentDevice.systemVersion}"
    }
}
