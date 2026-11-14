package dev.sayed.mehrabalmomen.data.platform.system

import android.os.Build
import dev.sayed.mehrabalmomen.domain.repository.platform.DeviceInfoProvider

class AndroidDeviceInfoProvider : DeviceInfoProvider {
    override fun getDeviceModel(): String {
        return "${Build.MANUFACTURER} ${Build.MODEL}"
    }

    override fun getOsVersion(): String {
        return "${Build.VERSION.RELEASE} (SDK ${Build.VERSION.SDK_INT})"
    }
}
