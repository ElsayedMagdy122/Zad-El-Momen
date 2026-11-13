package dev.sayed.mehrabalmomen.data.local

import androidx.room.Room
import androidx.room.RoomDatabase
import dev.sayed.mehrabalmomen.data.util.AppDatabase
import platform.Foundation.NSHomeDirectory

actual class DatabaseFactory {
    actual fun create(): RoomDatabase.Builder<AppDatabase> {
        val dbFilePath = NSHomeDirectory() + "/Documents/mehrab_database.db"
        return Room.databaseBuilder<AppDatabase>(
            name = dbFilePath
        ).fallbackToDestructiveMigration(true)
    }
}
