package dev.sayed.mehrabalmomen.data.local.quran.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SurahDto(
    @SerialName("id")
    val id: Int,
    @SerialName("name")
    val nameArabic: String,
    @SerialName("transliteration")
    val nameEnglish: String,
    @SerialName("total_verses")
    val totalVerses: Int,
    @SerialName("type")
    val type: String,
    @SerialName("verses")
    val verses: List<AyahDto>
)