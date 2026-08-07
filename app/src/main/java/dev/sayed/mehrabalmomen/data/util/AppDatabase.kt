package dev.sayed.mehrabalmomen.data.util

import androidx.room.Database
import androidx.room.RoomDatabase
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
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bookmarkDao(): BookmarkDao
    abstract fun downloadedReciterDao(): DownloadedReciterDao

}