package dev.sayed.mehrabalmomen.data.settings.dto

import kotlinx.serialization.Serializable

@Serializable
data class DeviceBrand(
    val manufacturer: String,
    val name: String,
    val batterySettings: BatterySettings
)