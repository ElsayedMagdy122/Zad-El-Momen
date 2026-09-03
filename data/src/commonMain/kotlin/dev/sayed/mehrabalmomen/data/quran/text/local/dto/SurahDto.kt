package dev.sayed.mehrabalmomen.data.quran.text.local.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SurahDto(
    @SerialName("id")
    val id: Int,
    @SerialName("name_arabic")
    val nameArabic: String,
    @SerialName("name_english")
    val nameEnglish: String,
    @SerialName("type")
    val type: String,
    @SerialName("total_verses")
    val totalVerses: Int,
    @SerialName("verses")
    val verses: List<AyahDto>
)
