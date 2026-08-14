package dev.sayed.mehrabalmomen.data.quran.audio.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class QuranAudioSurahDto(
    @SerialName("id")
    val id: Short,
    @SerialName("name_ar")
    val nameAr: String,
    @SerialName("name_en")
    val nameEn: String,
    @SerialName("verses_count")
    val versesCount: Short,
    @SerialName("created_at")
    val createdAt: String? = null
)