package dev.sayed.mehrabalmomen.data.settings.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LocalizedSettings(
    @SerialName("title") val title: String,
    @SerialName("message") val message: String,
    @SerialName("steps") val steps: List<String>
)