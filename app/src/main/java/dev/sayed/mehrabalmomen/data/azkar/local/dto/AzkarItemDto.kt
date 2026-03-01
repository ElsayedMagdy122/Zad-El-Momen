package dev.sayed.mehrabalmomen.data.azkar.local.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AzkarItemDto(
    @SerialName("content")
    val content: String,
    @SerialName("count")
    val count: String,
    @SerialName("description")
    val description: String? = null,
    @SerialName("reference")
    val reference: String? = null
)