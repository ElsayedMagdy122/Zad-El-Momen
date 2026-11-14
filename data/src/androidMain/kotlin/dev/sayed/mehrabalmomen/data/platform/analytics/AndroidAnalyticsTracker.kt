package dev.sayed.mehrabalmomen.data.platform.analytics

import dev.sayed.mehrabalmomen.domain.analytics.AnalyticsTracker

class AndroidAnalyticsTracker : AnalyticsTracker {

    override fun logEvent(name: String, params: Map<String, String>) {
    }

    override fun logScreen(screenName: String) {
        logEvent(
            name = "screen_view",
            params = mapOf("screen_name" to screenName)
        )
    }
}
