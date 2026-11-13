package dev.sayed.mehrabalmomen.data.local

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import dev.sayed.mehrabalmomen.data.util.AppDatabase
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

actual class DatabaseFactory : KoinComponent {
    private val context: Context by inject()

    actual fun create(): RoomDatabase.Builder<AppDatabase> {
        val appContext = context.applicationContext
        val dbFile = appContext.getDatabasePath("mehrab_database.db")
        return Room.databaseBuilder<AppDatabase>(
            context = appContext,
            name = dbFile.absolutePath
        )
        .addCallback(object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                db.execSQL("""
                    INSERT INTO reminders (title, description, hour, minute, recurrence, isEnabled, reminderType)
                    VALUES 
                    ('التنبيه قبل الصلاة', 'اقترب موعد الأذان، استعد للوضوء والصلاة', 0, 15, 'DAILY', 1, 'BEFORE_PRAYER'),
                    ('أذكار الصباح', 'لا تنس أذكار الصباح والتحصين', 7, 0, 'DAILY', 1, 'MORNING_AZKAR'),
                    ('أذكار المساء', 'لا تنس أذكار المساء لحفظك ونور صدرك', 17, 0, 'DAILY', 1, 'EVENING_AZKAR'),
                    ('الورد القرآني اليومي', 'حان وقت وردك القرآني اليومي', 20, 0, 'DAILY', 1, 'DAILY_WORD'),
                    ('سنن الجمعة', 'أكثر من الصلاة على النبي واقرأ سورة الكهف', 9, 0, 'WEEKLY_THURSDAY', 1, 'FRIDAY_SUNNAN')
                """.trimIndent())
            }

            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                db.execSQL("""
                    INSERT INTO reminders (title, description, hour, minute, recurrence, isEnabled, reminderType)
                    SELECT 'التنبيه قبل الصلاة', 'اقترب موعد الأذان، استعد للوضوء والصلاة', 0, 15, 'DAILY', 1, 'BEFORE_PRAYER'
                    WHERE NOT EXISTS (SELECT 1 FROM reminders WHERE reminderType = 'BEFORE_PRAYER')
                """.trimIndent())
            }
        })
        .fallbackToDestructiveMigration(true)
    }
}
