package dev.sayed.mehrabalmomen.data.settings.dto

import kotlinx.serialization.Serializable

@Serializable
data class BatterySettings(
    val arabic: LocalizedSettings,
    val english: LocalizedSettings
)