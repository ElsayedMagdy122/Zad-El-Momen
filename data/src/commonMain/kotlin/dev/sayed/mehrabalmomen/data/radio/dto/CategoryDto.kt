package dev.sayed.mehrabalmomen.data.radio.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CategoryDto(
    @SerialName("id")
    val id: String,

    @SerialName("name_ar")
    val nameAr: String,

    @SerialName("name_en")
    val nameEn: String
)