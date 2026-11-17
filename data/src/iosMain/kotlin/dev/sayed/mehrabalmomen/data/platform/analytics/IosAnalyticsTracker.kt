package dev.sayed.mehrabalmomen.data.platform.analytics

import dev.sayed.mehrabalmomen.domain.analytics.AnalyticsTracker

class IosAnalyticsTracker : AnalyticsTracker {
    override fun logEvent(name: String, params: Map<String, String>) {
        println("[Analytics] Event: $name, Params: $params")
    }

    override fun logScreen(screenName: String) {
        println("[Analytics] Screen: $screenName")
    }
}
