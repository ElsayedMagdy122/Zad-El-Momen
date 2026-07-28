package dev.sayed.mehrabalmomen.presentation.widget.prayer

import android.content.Context
import androidx.glance.appwidget.updateAll
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.CancellationException

/**
 * Performs the battery-safe periodic refresh that advances calculated countdown ring progress.
 *
 * @param appContext application context supplied by WorkManager.
 * @param workerParameters scheduling metadata supplied by WorkManager.
 */
class PrayerWidgetProgressRefreshWorker(
    appContext: Context,
    workerParameters: WorkerParameters,
) : CoroutineWorker(appContext, workerParameters) {
    /**
     * Recalculates and updates every installed prayer widget through the normal snapshot pipeline.
     *
     * Cancellation remains cooperative. Unexpected platform failures request WorkManager retry
     * with its normal backoff rather than creating a custom high-frequency loop.
     *
     * @return success after all widgets update, or retry after an unexpected platform failure.
     */
    override suspend fun doWork(): Result {
        return try {
            PrayerWidget().updateAll(applicationContext)
            Result.success()
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Exception) {
            Result.retry()
        }
    }
}
