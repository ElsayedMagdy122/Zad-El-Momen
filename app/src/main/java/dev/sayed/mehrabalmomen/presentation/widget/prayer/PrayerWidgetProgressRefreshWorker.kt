package dev.sayed.mehrabalmomen.presentation.widget.prayer

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.CancellationException
import org.koin.core.context.GlobalContext

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
            GlobalContext.get().get<PrayerWidgetUpdateCoordinator>().refreshAllIfInstalled()
            Result.success()
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Exception) {
            Result.retry()
        }
    }
}
