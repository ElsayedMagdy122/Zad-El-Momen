package dev.sayed.mehrabalmomen.data.local.quran.dto

import com.google.gson.annotations.SerializedName

data class TafseerDto(
    @SerializedName("number")
    val surahNumber: String,
    @SerializedName("aya")
    val ayahNumber: String,
    @SerializedName("text")
    val text: String
)