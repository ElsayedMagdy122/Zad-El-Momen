package dev.sayed.mehrabalmomen.data.settings.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DeviceBrand(
    @SerialName("manufacturer") val manufacturer: String,
    @SerialName("name") val name: String,
    @SerialName("dialog") val batterySettings: BatterySettings
)