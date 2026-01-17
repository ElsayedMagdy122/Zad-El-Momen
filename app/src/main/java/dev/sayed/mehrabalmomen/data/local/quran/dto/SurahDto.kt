package dev.sayed.mehrabalmomen.data.local.quran.dto

import com.google.gson.annotations.SerializedName

data class SurahDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val nameArabic: String,
    @SerializedName("transliteration")
    val nameEnglish: String,
    @SerializedName("total_verses")
    val totalVerses: Int,
    @SerializedName("type")
    val type: String,
    @SerializedName("verses")
    val verses: List<AyahDto>
)