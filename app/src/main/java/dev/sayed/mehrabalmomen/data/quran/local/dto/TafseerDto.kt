package dev.sayed.mehrabalmomen.data.quran.local.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TafseerDto(
    @SerialName("number")
    val surahNumber: String,
    @SerialName("aya")
    val ayahNumber: String,
    @SerialName("text")
    val text: String
)