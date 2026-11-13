package dev.sayed.mehrabalmomen.data.local

import androidx.room.RoomDatabase
import dev.sayed.mehrabalmomen.data.util.AppDatabase

expect class DatabaseFactory() {
    fun create(): RoomDatabase.Builder<AppDatabase>
}
