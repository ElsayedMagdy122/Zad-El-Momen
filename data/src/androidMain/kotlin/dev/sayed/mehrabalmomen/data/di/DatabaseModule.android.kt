package dev.sayed.mehrabalmomen.data.di

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import dev.sayed.mehrabalmomen.data.local.DatabaseFactory
import dev.sayed.mehrabalmomen.data.util.AppDatabase
import org.koin.core.module.Module
import org.koin.dsl.module

actual val databaseModule: Module = module {
    includes(databaseDaosModule)
    single<AppDatabase> {
        DatabaseFactory().create()
            .setDriver(BundledSQLiteDriver())
            .build()
    }
}
