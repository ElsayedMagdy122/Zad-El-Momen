package dev.sayed.mehrabalmomen.data.quran.audio.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "downloaded_reciters")
data class DownloadedReciterEntity(
    @PrimaryKey val id: Int,
    val surahId: Int, // The specific surah downloaded for this reciter
    val nameAr: String,
    val nameEn: String,
    val rewayaName: String,
    val baseAudioUrl: String,
    val localFilePath: String,
    val downloadTimestamp: Long = System.currentTimeMillis()
)
