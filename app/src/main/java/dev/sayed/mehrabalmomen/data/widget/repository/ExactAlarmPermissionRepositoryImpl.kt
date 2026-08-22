package dev.sayed.mehrabalmomen.data.widget.repository

import android.app.AlarmManager
import android.content.Context
import android.os.Build
import dev.sayed.mehrabalmomen.domain.repository.widget.ExactAlarmPermissionRepository

/**
 * Android implementation that reads exact alarm access from the system [AlarmManager].
 *
 * @property context application context used to obtain the platform alarm service.
 */
class ExactAlarmPermissionRepositoryImpl(
    private val context: Context,
) : ExactAlarmPermissionRepository {
    /**
     * Checks the platform's exact alarm access rule for the installed Android version.
     *
     * Android versions before Android 12 do not require this special access. On Android 12 and
     * later, the result comes from [AlarmManager.canScheduleExactAlarms].
     *
     * @return `true` when the application can schedule an exact alarm now.
     */
    override fun canScheduleExactAlarms(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return true

        val alarmManager = context.getSystemService(AlarmManager::class.java)
        return alarmManager.canScheduleExactAlarms()
    }
}
