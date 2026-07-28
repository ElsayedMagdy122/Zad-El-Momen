package dev.sayed.mehrabalmomen.presentation.widget.prayer

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

/**
 * Owns the one unique 15-minute ring refresh job shared by all prayer widget instances.
 *
 * @property workManager persistent Android scheduler used for approximate periodic refreshes.
 */
class PrayerWidgetProgressScheduler(
    private val workManager: WorkManager,
) {
    /**
     * Ensures that exactly one battery-safe periodic ring refresh is registered.
     *
     * Existing work is kept so widget updates and additional instances do not reset its cadence.
     */
    fun schedule() {
        val request = PeriodicWorkRequestBuilder<PrayerWidgetProgressRefreshWorker>(
            PROGRESS_REFRESH_INTERVAL_MINUTES,
            TimeUnit.MINUTES,
        ).build()
        workManager.enqueueUniquePeriodicWork(
            UNIQUE_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            request,
        )
    }

    /** Cancels the unique periodic ring refresh after the final widget instance is removed. */
    fun cancel() {
        workManager.cancelUniqueWork(UNIQUE_WORK_NAME)
    }

    companion object {
        /** Stable identity that prevents more than one periodic widget refresh from existing. */
        const val UNIQUE_WORK_NAME = "prayer_widget_progress_refresh"

        /** Minimum WorkManager periodic interval used for approximate ring progress updates. */
        const val PROGRESS_REFRESH_INTERVAL_MINUTES = 15L

        /**
         * Creates the production scheduler using the application's singleton WorkManager.
         *
         * @param context Android context used to resolve WorkManager.
         * @return scheduler that owns the unique prayer-widget periodic work.
         */
        fun from(context: Context): PrayerWidgetProgressScheduler {
            return PrayerWidgetProgressScheduler(WorkManager.getInstance(context.applicationContext))
        }
    }
}
