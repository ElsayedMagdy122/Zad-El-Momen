package dev.sayed.mehrabalmomen.domain.entity.quran.tilawah

data class QuranAudioTrack(
    val id: Long,
    val readerId: Int,
    val surahId: Int,
    val audioUrl: String
)