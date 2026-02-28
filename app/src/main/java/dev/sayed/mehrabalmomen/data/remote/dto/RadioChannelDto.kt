package dev.sayed.mehrabalmomen.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RadioChannelDto(
    @SerialName("id")
    val id: Int,

    @SerialName("name_ar")
    val nameAr: String,

    @SerialName("name_en")
    val nameEn: String,

    @SerialName("stream_url")
    val streamUrl: String,

    @SerialName("created_at")
    val createdAt: String
)