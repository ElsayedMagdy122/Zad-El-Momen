package dev.sayed.mehrabalmomen.data.quran.audio.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class QuranAudioReaderDto(
    @SerialName("id")
    val id: Int,
    @SerialName("name_ar")
    val nameAr: String,
    @SerialName("name_en")
    val nameEn: String,
    @SerialName("base_audio_url")
    val baseAudioUrl: String,
    @SerialName("surahs_count")
    val surahsCount: Short? = null,
    @SerialName("rewaya_id")
    val rewayaId: Int? = null,
    @SerialName("created_at")
    val createdAt: String? = null
)