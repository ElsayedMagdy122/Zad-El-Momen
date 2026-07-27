package dev.sayed.mehrabalmomen.presentation.widget.prayer

import android.content.Context
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.fillMaxSize
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.presentation.base.MainActivity
import dev.sayed.mehrabalmomen.presentation.widget.prayer.componenets.MediumAltPrayerWidget
import dev.sayed.mehrabalmomen.presentation.widget.prayer.componenets.MediumPrayerWidget
import dev.sayed.mehrabalmomen.presentation.widget.prayer.componenets.PrayerWidgetMessage
import dev.sayed.mehrabalmomen.presentation.widget.prayer.componenets.SmallPrayerWidget

class PrayerWidget : GlanceAppWidget() {
    override val sizeMode: SizeMode = SizeMode.Responsive(
        setOf(
            SmallWidgetSize,
            MediumWidgetSize,
            MediumAltWidgetSize,
        ),
    )

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            val localContext = LocalContext.current
            val state = PrayerWidgetFixture.ready(localContext)
            val size = LocalSize.current
            val description = localContext.getString(
                R.string.prayer_widget_content_description,
                state.nextPrayerName,
                state.countdown,
            )

            Box(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .clickable(actionStartActivity<MainActivity>()),
                contentAlignment = Alignment.Center,
            ) {
                when {
                    state.status != PrayerWidgetStatus.READY -> PrayerWidgetMessage(state.status)
                    size.width >= MediumAltWidgetSize.width &&
                        size.height >= MediumAltWidgetSize.height -> {
                        MediumAltPrayerWidget(state)
                    }
                    size.width >= MediumWidgetSize.width -> MediumPrayerWidget(state, description)
                    else -> SmallPrayerWidget(state, description)
                }
            }
        }
    }
}

private val SmallWidgetSize = DpSize(width = 250.dp, height = 110.dp)
private val MediumWidgetSize = DpSize(width = 300.dp, height = 110.dp)
private val MediumAltWidgetSize = DpSize(width = 300.dp, height = 190.dp)
