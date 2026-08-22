package dev.sayed.mehrabalmomen.presentation.widget.prayer.componenets

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.LocalContext
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.presentation.widget.prayer.PrayerWidgetUiState
import dev.sayed.mehrabalmomen.presentation.widget.prayer.localizedString
import dev.sayed.mehrabalmomen.presentation.widget.prayer.PrayerWidgetStatus

@Composable
internal fun PrayerWidgetMessage(state: PrayerWidgetUiState) {
    val context = LocalContext.current
    val message = when (state.status) {
        PrayerWidgetStatus.READY -> ""
        PrayerWidgetStatus.LOADING -> state.localizedString(context, R.string.prayer_widget_loading)
        PrayerWidgetStatus.NEEDS_LOCATION -> {
            state.localizedString(context, R.string.prayer_widget_open_location)
        }
        PrayerWidgetStatus.ERROR -> state.localizedString(context, R.string.prayer_widget_error)
        PrayerWidgetStatus.EXACT_ALARM_PERMISSION_REQUIRED -> {
            state.localizedString(context, R.string.prayer_widget_exact_alarm_permission)
        }
    }

    Text(
        text = message,
        style = TextStyle(
            color = WidgetText,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
        ),
        modifier = GlanceModifier
            .background(WidgetWhite)
            .cornerRadius(16.dp)
            .padding(16.dp),
    )
}
