package dev.sayed.mehrabalmomen.domain.model.audio

/**
 * Domain model for the last selected or preferred reciter.
 */
data class ReciterPreference(
    val id: Int,
    val nameAr: String,
    val nameEn: String,
    val baseAudioUrl: String,
    val rewayaName: String
)
