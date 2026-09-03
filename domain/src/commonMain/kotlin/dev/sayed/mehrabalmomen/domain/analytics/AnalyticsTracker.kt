package dev.sayed.mehrabalmomen.domain.analytics

/**
 * Interface for tracking application events and screen views.
 */
interface AnalyticsTracker {
    fun logEvent(
        name: String,
        params: Map<String, String> = emptyMap()
    )
    fun logScreen(screenName: String)
}
