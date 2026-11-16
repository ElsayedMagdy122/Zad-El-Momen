package dev.sayed.mehrabalmomen.data.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import dev.sayed.mehrabalmomen.data.util.AppDatabase
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

private object ContextHolder : KoinComponent {
    fun getContext(): Context = get()
}

actual fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
    val context = ContextHolder.getContext()
    val appContext = context.applicationContext
    val dbFile = appContext.getDatabasePath("mehrab_database.db")
    return Room.databaseBuilder<AppDatabase>(
        context = appContext,
        name = dbFile.absolutePath
    )
}
