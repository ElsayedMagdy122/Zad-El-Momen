package dev.sayed.mehrabalmomen.data.platform.system

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.provider.Settings
import dev.sayed.mehrabalmomen.domain.repository.platform.DeviceInfoProvider
import java.security.MessageDigest

/**
 * Android implementation of [DeviceInfoProvider].
 */
class AndroidDeviceInfoProvider(
    private val context: Context
) : DeviceInfoProvider {
    override fun getDeviceModel(): String {
        return "${Build.MANUFACTURER} ${Build.MODEL}"
    }

    override fun getOsVersion(): String {
        return "${Build.VERSION.RELEASE} (SDK ${Build.VERSION.SDK_INT})"
    }

    @SuppressLint("HardwareIds")
    override fun getDeviceId(): String {
        val androidId = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        ) ?: "unknown_device"
        return toSHA256(androidId)
    }

    private fun toSHA256(input: String): String {
        val bytes = MessageDigest
            .getInstance("SHA-256")
            .digest(input.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
