package dev.sayed.mehrabalmomen.data.settings.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BatterySettings(
    @SerialName("ar") val arabic: LocalizedSettings,
    @SerialName("en") val english: LocalizedSettings
)