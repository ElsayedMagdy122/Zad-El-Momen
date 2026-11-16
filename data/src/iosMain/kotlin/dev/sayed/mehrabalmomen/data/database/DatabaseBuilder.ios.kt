package dev.sayed.mehrabalmomen.data.database

import androidx.room.Room
import androidx.room.RoomDatabase
import dev.sayed.mehrabalmomen.data.util.AppDatabase
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

@OptIn(ExperimentalForeignApi::class)
actual fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
    val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = true,
        error = null
    )
    val dbFilePath = documentDirectory?.path + "/mehrab_database.db"
    return Room.databaseBuilder<AppDatabase>(
        name = dbFilePath
    )
}

