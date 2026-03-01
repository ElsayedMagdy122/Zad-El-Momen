package dev.sayed.mehrabalmomen.data.util

import androidx.room.Database
import androidx.room.RoomDatabase
import dev.sayed.mehrabalmomen.data.quran.local.dao.BookmarkDao
import dev.sayed.mehrabalmomen.data.quran.local.dto.BookmarkEntity

@Database(
    entities = [
        BookmarkEntity::class,
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bookmarkDao(): BookmarkDao

}