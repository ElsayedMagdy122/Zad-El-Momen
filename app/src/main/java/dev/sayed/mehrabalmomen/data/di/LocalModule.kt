package dev.sayed.mehrabalmomen.data.di

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import dev.sayed.mehrabalmomen.data.azkar.local.AzkarLocalDataSource
import dev.sayed.mehrabalmomen.data.util.AppDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
private const val DATASTORE_NAME = "location_prefs"
private const val RECITATION_DATASTORE_NAME = "recitation_prefs"
private const val AZKAR_DATASTORE_NAME = "azkar_prefs"

val Context.locationDataStore by preferencesDataStore(name = DATASTORE_NAME)
val Context.recitationDataStore by preferencesDataStore(name = RECITATION_DATASTORE_NAME)
val Context.azkarDataStore by preferencesDataStore(name = AZKAR_DATASTORE_NAME)

val localModule = module {
    single { get<Context>().locationDataStore }
    single(qualifier = org.koin.core.qualifier.named("azkar_prefs")) {
        get<Context>().azkarDataStore
    }
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
        )
        .addCallback(object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                db.execSQL("""
                    INSERT INTO reminders (title, description, hour, minute, recurrence, isEnabled, reminderType)
                    VALUES 
                    ('أذكار الصباح', 'لا تنس أذكار الصباح والتحصين', 7, 0, 'DAILY', 1, 'MORNING_AZKAR'),
                    ('أذكار المساء', 'لا تنس أذكار المساء لحفظك ونور صدرك', 17, 0, 'DAILY', 1, 'EVENING_AZKAR'),
                    ('الورد القرآني اليومي', 'حان وقت وردك القرآني اليومي', 20, 0, 'DAILY', 1, 'DAILY_WORD'),
                    ('سنن الجمعة', 'أكثر من الصلاة على النبي واقرأ سورة الكهف', 9, 0, 'WEEKLY_THURSDAY', 1, 'FRIDAY_SUNNAN')
                """.trimIndent())
            }
        })
        .fallbackToDestructiveMigration(true)
        .build()
    }
    single { get<AppDatabase>().bookmarkDao() }
    single { get<AppDatabase>().downloadedReciterDao() }
    single { get<AppDatabase>().azkarDao() }
    single { get<AppDatabase>().reminderDao() }
    single<AzkarLocalDataSource> { 
        AzkarLocalDataSource(
            context = androidContext(),
            azkarDao = get(),
            json = get()
        )
    }
}
