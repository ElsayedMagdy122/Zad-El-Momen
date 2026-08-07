package dev.sayed.mehrabalmomen.data.di

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import dev.sayed.mehrabalmomen.data.azkar.local.AzkarLocalDataSource
import dev.sayed.mehrabalmomen.data.util.AppDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
private const val DATASTORE_NAME = "location_prefs"
private const val RECITATION_DATASTORE_NAME = "recitation_prefs"

val Context.locationDataStore by preferencesDataStore(name = DATASTORE_NAME)
val Context.recitationDataStore by preferencesDataStore(name = RECITATION_DATASTORE_NAME)

val localModule = module {
    single { get<Context>().locationDataStore }
    single(qualifier = org.koin.core.qualifier.named("recitation_prefs")) {
        get<Context>().recitationDataStore
    }
    single { 
        dev.sayed.mehrabalmomen.data.settings.local.RecitationPreferences(
            get(qualifier = org.koin.core.qualifier.named("recitation_prefs"))
        )
    }
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "mehrab_database"
        ).build()
    }
    single { get<AppDatabase>().bookmarkDao() }
    single { get<AppDatabase>().downloadedReciterDao() }
    single<AzkarLocalDataSource> { AzkarLocalDataSource(get(), get()) }
}