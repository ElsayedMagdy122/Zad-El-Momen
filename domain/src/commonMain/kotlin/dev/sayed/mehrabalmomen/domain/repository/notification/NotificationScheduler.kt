package dev.sayed.mehrabalmomen.domain.repository.notification

import dev.sayed.mehrabalmomen.domain.model.ReminderNotification

/**
 * Interface defining the contract for displaying system notifications.
 */
interface NotificationScheduler {

    /**
     * Displays a notification to the user.
     * @param notification Domain model containing notification content.
     */
    fun showReminder(notification: ReminderNotification)

    /**
     * Checks if the platform has permission to show notifications (required for Android 13+).
     */
    fun hasPermission(): Boolean
}
