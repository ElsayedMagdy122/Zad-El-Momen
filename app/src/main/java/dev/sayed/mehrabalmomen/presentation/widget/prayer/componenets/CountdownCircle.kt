package dev.sayed.mehrabalmomen.presentation.widget.prayer.componenets

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.LocalContext
import androidx.glance.appwidget.AndroidRemoteViews
import androidx.glance.appwidget.ExperimentalGlanceRemoteViewsApi
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.presentation.widget.prayer.PrayerWidgetCountdownRemoteViewsFactory
import dev.sayed.mehrabalmomen.presentation.widget.prayer.PrayerWidgetCountdownRingRemoteViewsFactory
import dev.sayed.mehrabalmomen.presentation.widget.prayer.PrayerWidgetStatus
import dev.sayed.mehrabalmomen.presentation.widget.prayer.PrayerWidgetUiState
import dev.sayed.mehrabalmomen.presentation.widget.prayer.localizedString

@OptIn(ExperimentalGlanceRemoteViewsApi::class)
@Composable
internal fun CountdownCircle(
    state: PrayerWidgetUiState,
    modifier: GlanceModifier,
    contentDescription: String,
) {
    val context = LocalContext.current
    val countdownFactory = PrayerWidgetCountdownRemoteViewsFactory()
    val ringFactory = PrayerWidgetCountdownRingRemoteViewsFactory()

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        AndroidRemoteViews(
            remoteViews = ringFactory.create(
                context = context,
                progress = state.countdownProgress,
                contentDescription = contentDescription,
            ),
            modifier = GlanceModifier.fillMaxSize(),
        )
        Column(
            modifier = GlanceModifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = state.localizedString(context, R.string.prayer_widget_next_prayer),
                style = TextStyle(
                    color = WidgetGold,
                    fontSize = 6.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                ),
                maxLines = 1,
            )
            Spacer(modifier = GlanceModifier.height(3.dp))
            Text(
                text = state.nextPrayerName.uppercase(),
                style = TextStyle(
                    color = WidgetWhite,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                ),
                maxLines = 1,
                modifier = GlanceModifier
                    .background(WidgetGold)
                    .cornerRadius(16.dp)
                    .padding(horizontal = 10.dp, vertical = 2.dp),
            )
            Spacer(modifier = GlanceModifier.height(4.dp))
            if (
                state.status == PrayerWidgetStatus.READY &&
                countdownFactory.canStart(state.targetEpochMillis)
            ) {
                AndroidRemoteViews(
                    remoteViews = countdownFactory.create(context, state.targetEpochMillis),
                    modifier = GlanceModifier.height(24.dp),
                )
            } else {
                Text(
                    text = state.countdown.asHoursAndMinutes(),
                    style = TextStyle(
                        color = WidgetWhite,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                    ),
                    maxLines = 1,
                )
            }
        }
    }
}
