package dev.sayed.mehrabalmomen.data.database

import androidx.room.RoomDatabase
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL

val roomDatabaseCallback = object : RoomDatabase.Callback() {
    override fun onCreate(connection: SQLiteConnection) {
        super.onCreate(connection)
        connection.execSQL("""
            INSERT INTO reminders (title, description, hour, minute, recurrence, isEnabled, reminderType)
            VALUES 
            ('التنبيه قبل الصلاة', 'اقترب موعد الأذان، استعد للوضوء والصلاة', 0, 15, 'DAILY', 1, 'BEFORE_PRAYER'),
            ('أذكار الصباح', 'لا تنس أذكار الصباح والتحصين', 7, 0, 'DAILY', 1, 'MORNING_AZKAR'),
            ('أذكار المساء', 'لا تنس أذكار المساء لحفظك ونور صدرك', 17, 0, 'DAILY', 1, 'EVENING_AZKAR'),
            ('الورد القرآني اليومي', 'حان وقت وردك القرآني اليومي', 20, 0, 'DAILY', 1, 'DAILY_WORD'),
            ('سنن الجمعة', 'أكثر من الصلاة على النبي واقرأ سورة الكهف', 9, 0, 'WEEKLY_THURSDAY', 1, 'FRIDAY_SUNNAN')
        """.trimIndent())
    }

    override fun onOpen(connection: SQLiteConnection) {
        super.onOpen(connection)
        connection.execSQL("""
            INSERT INTO reminders (title, description, hour, minute, recurrence, isEnabled, reminderType)
            SELECT 'التنبيه قبل الصلاة', 'اقترب موعد الأذان، استعد للوضوء والصلاة', 0, 15, 'DAILY', 1, 'BEFORE_PRAYER'
            WHERE NOT EXISTS (SELECT 1 FROM reminders WHERE reminderType = 'BEFORE_PRAYER')
        """.trimIndent())
    }
}
