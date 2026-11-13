package dev.sayed.mehrabalmomen.presentation.screen.qiblah

import android.hardware.GeomagneticField

actual fun getMagneticDeclination(
    latitude: Double,
    longitude: Double,
    altitude: Double,
    timeMs: Long
): Float {
    if (latitude == 0.0 && longitude == 0.0) return 0f
    return try {
        val field = GeomagneticField(
            latitude.toFloat(),
            longitude.toFloat(),
            altitude.toFloat(),
            timeMs
        )
        field.declination
    } catch (_: Exception) {
        0f
    }
}
