package dev.sayed.mehrabalmomen.domain.repository.prayer

import dev.sayed.mehrabalmomen.domain.model.AlarmTask

/**
 * Interface defining the contract for scheduling deferred tasks using system alarms.
 * This abstraction allows the domain layer to request scheduling without knowing about
 * Android's AlarmManager or Intents.
 */
interface AlarmScheduler {

    /**
     * Schedules a task to be executed at a specific time.
     *
     * @param requestCode Unique identifier for the alarm instance.
     * @param triggerAtMillis Time in milliseconds when the alarm should fire.
     * @param task The specific type of task to be performed.
     */
    fun schedule(
        requestCode: Int,
        triggerAtMillis: Long,
        task: AlarmTask
    )

    /**
     * Cancels a previously scheduled task.
     */
    fun cancel(requestCode: Int, task: AlarmTask)

    /**
     * Checks if the platform has granted permission to schedule exact alarms.
     */
    fun hasPermission(): Boolean
}
