package dev.sayed.mehrabalmomen.presentation.widget.prayer

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import dev.sayed.mehrabalmomen.presentation.base.MainActivity
import androidx.core.net.toUri

internal const val PRAYER_WIDGET_DESTINATION_EXTRA = "prayer_widget_destination"
internal const val PRAYER_WIDGET_DESTINATION_PRAYER_TIMES = "prayer_times"

/**
 * Creates the app launch intent used when the widget should open the prayer-times screen.
 *
 * @receiver Android context used to target [MainActivity].
 * @return intent carrying the widget destination consumed by the app navigation layer.
 */
internal fun Context.prayerWidgetLaunchIntent(): Intent {
    return Intent(this, MainActivity::class.java).apply {
        action = Intent.ACTION_VIEW
        putExtra(PRAYER_WIDGET_DESTINATION_EXTRA, PRAYER_WIDGET_DESTINATION_PRAYER_TIMES)
        addFlags(
            Intent.FLAG_ACTIVITY_NEW_TASK or
                Intent.FLAG_ACTIVITY_CLEAR_TOP or
                Intent.FLAG_ACTIVITY_SINGLE_TOP,
        )
    }
}

/**
 * Creates the settings intent used when exact-alarm access is required for live countdown updates.
 *
 * @receiver Android context used to scope the settings screen to this package when possible.
 * @return platform settings intent for exact alarms on Android 12+, or app details on older APIs.
 */
internal fun Context.exactAlarmSettingsIntent(): Intent {
    val packageUri = "package:$packageName".toUri()
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
            data = packageUri
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    } else {
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = packageUri
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }
}

/**
 * Reads the widget destination from an activity launch intent.
 *
 * @receiver nullable intent received by [MainActivity].
 * @return the destination identifier requested by the widget, or `null` for normal app launches.
 */
internal fun Intent?.prayerWidgetDestination(): String? {
    return this?.getStringExtra(PRAYER_WIDGET_DESTINATION_EXTRA)
}
