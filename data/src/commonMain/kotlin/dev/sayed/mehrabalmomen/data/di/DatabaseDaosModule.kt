package dev.sayed.mehrabalmomen.data.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import dev.sayed.mehrabalmomen.data.azkar.local.AzkarLocalDataSource
import dev.sayed.mehrabalmomen.data.local.DataStoreFactory
import dev.sayed.mehrabalmomen.data.settings.local.RecitationPreferences
import dev.sayed.mehrabalmomen.data.util.AppDatabase
import org.koin.core.qualifier.named
import org.koin.dsl.module

val databaseDaosModule = module {
    single<DataStore<Preferences>> { DataStoreFactory().create("location_prefs") }
    single<DataStore<Preferences>>(qualifier = named("azkar_prefs")) {
        DataStoreFactory().create("azkar_prefs")
    }
    single<DataStore<Preferences>>(qualifier = named("recitation_prefs")) {
        DataStoreFactory().create("recitation_prefs")
    }

    single {
        RecitationPreferences(
            get(qualifier = named("recitation_prefs"))
        )
    }

    single { get<AppDatabase>().bookmarkDao() }
    single { get<AppDatabase>().downloadedReciterDao() }
    single { get<AppDatabase>().azkarDao() }
    single { get<AppDatabase>().reminderDao() }

    single {
        AzkarLocalDataSource(
            assetReader = get(),
            azkarDao = get(),
            json = get()
        )
    }
}
