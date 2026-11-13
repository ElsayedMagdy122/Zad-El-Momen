package dev.sayed.mehrabalmomen.data.quran.audio.local.entity

import androidx.room.Entity
import kotlin.time.Clock

@Entity(
    tableName = "downloaded_reciters",
    primaryKeys = ["id", "surahId"]
)
data class DownloadedReciterEntity(
    val id: Int,
    val surahId: Int, // The specific surah downloaded for this reciter
    val nameAr: String,
    val nameEn: String,
    val rewayaName: String,
    val baseAudioUrl: String,
    val localFilePath: String,
    val downloadTimestamp: Long = Clock.System.now().toEpochMilliseconds()
)
