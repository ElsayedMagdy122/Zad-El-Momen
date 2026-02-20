package dev.sayed.mehrabalmomen.data.local.quran

import androidx.room.Database
import androidx.room.RoomDatabase
import dev.sayed.mehrabalmomen.data.local.quran.dto.BookmarkEntity

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