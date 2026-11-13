package dev.sayed.mehrabalmomen.presentation.screen.qiblah

expect fun getMagneticDeclination(
    latitude: Double,
    longitude: Double,
    altitude: Double = 0.0,
    timeMs: Long
): Float
