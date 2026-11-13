package dev.sayed.mehrabalmomen.domain.analytics

/**
 * Interface for tracking application events and screen views.
 * Isolates the application from specific analytics providers like Firebase.
 */
interface AnalyticsTracker {

    /**
     * Logs a custom event.
     * @param name The name of the event.
     * @param params Optional parameters associated with the event.
     */
    fun logEvent(
        name: String,
        params: Map<String, String> = emptyMap()
    )

    /**
     * Logs a screen view.
     * @param screenName The semantic name of the screen.
     */
    fun logScreen(screenName: String)
}
