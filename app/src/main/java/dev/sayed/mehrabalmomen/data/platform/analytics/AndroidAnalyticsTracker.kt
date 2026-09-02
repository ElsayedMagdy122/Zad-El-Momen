package dev.sayed.mehrabalmomen.data.platform.analytics

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent
import dev.sayed.mehrabalmomen.domain.analytics.AnalyticsTracker

/**
 * Android implementation of [AnalyticsTracker] using Firebase Analytics.
 */
class AndroidAnalyticsTracker(
    private val firebaseAnalytics: FirebaseAnalytics
) : AnalyticsTracker {

    override fun logEvent(name: String, params: Map<String, String>) {
        firebaseAnalytics.logEvent(name) {
            params.forEach { (key, value) ->
                param(key, value)
            }
        }
    }

    override fun logScreen(screenName: String) {
        logEvent(
            name = "screen_view",
            params = mapOf("screen_name" to screenName)
        )
    }
}
