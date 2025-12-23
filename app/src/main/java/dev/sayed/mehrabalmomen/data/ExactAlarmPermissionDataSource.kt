package dev.sayed.mehrabalmomen.data

import android.app.AlarmManager
import android.content.Context
import android.os.Build

class ExactAlarmPermissionDataSource(
    private val context: Context
) {

    fun hasPermission(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return true
        return context
            .getSystemService(AlarmManager::class.java)
            .canScheduleExactAlarms()
    }

}