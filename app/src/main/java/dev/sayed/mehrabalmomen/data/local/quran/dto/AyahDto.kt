package dev.sayed.mehrabalmomen.data.local.quran.dto

import com.google.gson.annotations.SerializedName


data class AyahDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("aya_no")
    val ayahNumber: Int,
    @SerializedName("text")
    val text: String,
    @SerializedName("page")
    val page: Int,
    @SerializedName("jozz")
    val juzNumber: Int
)