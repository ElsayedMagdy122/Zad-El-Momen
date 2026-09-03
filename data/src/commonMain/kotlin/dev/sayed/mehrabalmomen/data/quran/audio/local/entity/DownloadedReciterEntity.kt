package dev.sayed.mehrabalmomen.data.quran.audio.local.entity

import androidx.room.Entity
import dev.sayed.mehrabalmomen.domain.utils.getCurrentTimeMillis

@Entity(
    tableName = "downloaded_reciters",
    primaryKeys = ["id", "surahId"]
)
data class DownloadedReciterEntity(
    val id: Int,
    val surahId: Int,
    val nameAr: String,
    val nameEn: String,
    val rewayaName: String,
    val baseAudioUrl: String,
    val localFilePath: String,
    val downloadTimestamp: Long = getCurrentTimeMillis()
)
