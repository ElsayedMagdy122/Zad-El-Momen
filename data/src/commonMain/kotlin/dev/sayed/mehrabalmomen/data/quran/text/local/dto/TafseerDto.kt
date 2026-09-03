package dev.sayed.mehrabalmomen.data.quran.text.local.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TafseerDto(
    @SerialName("index")
    val index: String,
    @SerialName("surah")
    val surahNumber: String,
    @SerialName("ayah")
    val ayahNumber: String,
    @SerialName("text")
    val text: String
)
