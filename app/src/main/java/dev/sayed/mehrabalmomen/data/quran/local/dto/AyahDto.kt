package dev.sayed.mehrabalmomen.data.quran.local.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AyahDto(
    @SerialName("id")
    val id: Int,
    @SerialName("aya_no")
    val ayahNumber: Int,
    @SerialName("text")
    val text: String,
    @SerialName("text_emlaey")
    val textEmlaey: String,
    @SerialName("page")
    val page: Int,
    @SerialName("jozz")
    val juzNumber: Int
)