package dev.sayed.mehrabalmomen.presentation.widget.prayer

import android.content.Context
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.action.Action
import androidx.glance.action.clickable
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.fillMaxSize
import dev.sayed.mehrabalmomen.domain.usecase.GetPrayerWidgetSnapshotUseCase
import dev.sayed.mehrabalmomen.presentation.widget.prayer.componenets.MediumAltPrayerWidget
import dev.sayed.mehrabalmomen.presentation.widget.prayer.componenets.MediumPrayerWidget
import dev.sayed.mehrabalmomen.presentation.widget.prayer.componenets.PrayerWidgetMessage
import dev.sayed.mehrabalmomen.presentation.widget.prayer.componenets.SmallPrayerWidget
import dev.sayed.mehrabalmomen.presentation.widget.prayer.mapper.PrayerWidgetSnapshotMapper
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.context.GlobalContext

class PrayerWidget : GlanceAppWidget() {
    override val sizeMode: SizeMode = SizeMode.Responsive(
        setOf(
            SmallWidgetSize,
            MediumWidgetSize,
            MediumAltWidgetSize,
        ),
    )

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val state = loadState()

        provideContent {
            val localContext = LocalContext.current
            val size = LocalSize.current
            val description = state.contentDescription(localContext)

            Box(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .clickable(state.clickAction(localContext)),
                contentAlignment = Alignment.Center,
            ) {
                when {
                    state.status.isMessageOnly() -> PrayerWidgetMessage(state)
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

    /**
     * Loads the current widget UI state and coordinates its next exact prayer-boundary refresh.
     *
     * @return mapped widget UI state, a permission state when scheduling is rejected, or an error
     * state when dependency resolution or mapping fails.
     */
    private suspend fun loadState(): PrayerWidgetUiState {
        return try {
            withContext(Dispatchers.IO) {
                val koin = GlobalContext.get()
                val snapshot = koin.get<GetPrayerWidgetSnapshotUseCase>().invoke()
                val state = koin.get<PrayerWidgetSnapshotMapper>().map(snapshot)
                val boundaryScheduler = koin.get<PrayerWidgetBoundaryScheduler>()
                if (state.status == PrayerWidgetStatus.READY) {
                    val scheduled = boundaryScheduler.schedule(state.targetEpochMillis)
                    if (scheduled) {
                        state
                    } else {
                        state.copy(status = PrayerWidgetStatus.EXACT_ALARM_PERMISSION_REQUIRED)
                    }
                } else {
                    boundaryScheduler.cancel()
                    state
                }
            }
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Exception) {
            PrayerWidgetUiState(status = PrayerWidgetStatus.ERROR)
        }
    }
}

/**
 * Selects the launcher action that best matches the current widget status.
 *
 * @receiver widget UI state whose status determines the destination.
 * @param context Android context used to create the activity intent.
 * @return Glance action that opens either the app prayer screen or exact-alarm settings.
 */
private fun PrayerWidgetUiState.clickAction(context: Context): Action {
    return if (status == PrayerWidgetStatus.EXACT_ALARM_PERMISSION_REQUIRED) {
        actionStartActivity(context.exactAlarmSettingsIntent())
    } else {
        actionStartActivity(context.prayerWidgetLaunchIntent())
    }
}

/**
 * Determines whether a status should render only the centered fallback message.
 *
 * @receiver widget status selected by the mapped UI state.
 * @return `true` for non-content states, or `false` when calculated prayer content is available.
 */
private fun PrayerWidgetStatus.isMessageOnly(): Boolean {
    return this == PrayerWidgetStatus.LOADING ||
        this == PrayerWidgetStatus.NEEDS_LOCATION ||
        this == PrayerWidgetStatus.ERROR
}

private val SmallWidgetSize = DpSize(width = 250.dp, height = 110.dp)
private val MediumWidgetSize = DpSize(width = 300.dp, height = 110.dp)
private val MediumAltWidgetSize = DpSize(width = 300.dp, height = 190.dp)
