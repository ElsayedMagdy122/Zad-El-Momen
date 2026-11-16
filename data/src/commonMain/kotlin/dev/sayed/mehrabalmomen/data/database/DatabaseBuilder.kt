package dev.sayed.mehrabalmomen.data.database

import androidx.room.RoomDatabase
import dev.sayed.mehrabalmomen.data.util.AppDatabase

expect fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase>
