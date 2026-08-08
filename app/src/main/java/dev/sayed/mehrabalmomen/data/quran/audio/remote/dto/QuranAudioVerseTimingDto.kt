package dev.sayed.mehrabalmomen.data.quran.audio.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class QuranAudioVerseTimingDto(
    @SerialName("reader_id")
    val readerId: Int,
    @SerialName("surah_id")
    val surahId: Short,
    @SerialName("verse_number")
    val verseNumber: Int,
    @SerialName("start_time_ms")
    val startTimeMs: Long,
    @SerialName("end_time_ms")
    val endTimeMs: Long,
    @SerialName("created_at")
    val createdAt: String? = null
)