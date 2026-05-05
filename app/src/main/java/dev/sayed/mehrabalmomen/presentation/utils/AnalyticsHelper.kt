package dev.sayed.mehrabalmomen.presentation.utils

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent

class AnalyticsHelper(
    private val firebaseAnalytics: FirebaseAnalytics
) {

    fun logEvent(
        name: String,
        params: Map<String, String> = emptyMap()
    ) {
        firebaseAnalytics.logEvent(name) {
            params.forEach { (key, value) ->
                param(key, value)
            }
        }
    }

    fun logScreen(screenName: String) {
        logEvent(
            name = "screen_view",
            params = mapOf("screen_name" to screenName)
        )
    }
}