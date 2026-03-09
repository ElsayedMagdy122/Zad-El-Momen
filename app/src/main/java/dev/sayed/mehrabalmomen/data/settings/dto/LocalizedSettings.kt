package dev.sayed.mehrabalmomen.data.settings.dto

import kotlinx.serialization.Serializable

@Serializable
data class LocalizedSettings(
    val header: String,
    val description: String,
    val instructions: List<String>
)