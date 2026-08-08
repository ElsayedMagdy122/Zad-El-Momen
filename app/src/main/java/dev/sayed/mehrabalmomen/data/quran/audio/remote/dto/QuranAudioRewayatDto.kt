package dev.sayed.mehrabalmomen.data.quran.audio.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class QuranAudioRewayatDto(
    @SerialName("id")
    val id: Int,
    @SerialName("name_ar")
    val nameAr: String,
    @SerialName("name_en")
    val nameEn: String
)



