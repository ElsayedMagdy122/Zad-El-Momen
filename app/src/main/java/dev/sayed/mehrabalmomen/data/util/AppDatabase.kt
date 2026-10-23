package dev.sayed.mehrabalmomen.data.util

import androidx.room.Database
import androidx.room.RoomDatabase
import dev.sayed.mehrabalmomen.data.azkar.local.dao.AzkarDao
import dev.sayed.mehrabalmomen.data.azkar.local.entity.AzkarCustomCategoryEntity
import dev.sayed.mehrabalmomen.data.azkar.local.entity.AzkarCustomItemEntity
import dev.sayed.mehrabalmomen.data.azkar.local.entity.AzkarHistoryEntity
import dev.sayed.mehrabalmomen.data.azkar.local.entity.AzkarStatsEntity
import dev.sayed.mehrabalmomen.data.quran.audio.local.dao.DownloadedReciterDao
import dev.sayed.mehrabalmomen.data.quran.audio.local.entity.DownloadedReciterEntity
import dev.sayed.mehrabalmomen.data.quran.audio.local.entity.VerseTimingEntity
import dev.sayed.mehrabalmomen.data.quran.text.local.dao.BookmarkDao
import dev.sayed.mehrabalmomen.data.quran.text.local.dto.BookmarkEntity

@Database(
    entities = [
        BookmarkEntity::class,
        DownloadedReciterEntity::class,
        VerseTimingEntity::class,
        AzkarCustomCategoryEntity::class,
        AzkarCustomItemEntity::class,
        AzkarStatsEntity::class,
        AzkarHistoryEntity::class,
    ],
    // new version 3 not 4
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bookmarkDao(): BookmarkDao
    abstract fun downloadedReciterDao(): DownloadedReciterDao
    abstract fun azkarDao(): AzkarDao
}