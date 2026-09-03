package dev.sayed.mehrabalmomen.presentation.screen.reminders.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.base.LocalAppLocale
import dev.sayed.mehrabalmomen.presentation.base.localizedString
import dev.sayed.mehrabalmomen.presentation.base.toLocalizedDigits
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlin.math.abs

@Composable
fun TimeWheelPicker1(
    modifier: Modifier = Modifier,
    initialHour: Int = 8,
    initialMinute: Int = 30,
    onTimeChanged: (Int, Int) -> Unit
) {
    val hours = (1..12).toList()
    val minutes = (0..59).toList()

    val amString = localizedString(R.string.am)
    val pmString = localizedString(R.string.pm)
    val amPmItems = remember(amString, pmString) { listOf(amString, pmString) }

    val initialAmPm = if (initialHour >= 12) 1 else 0
    val initial12Hour = when {
        initialHour == 0 -> 12
        initialHour > 12 -> initialHour - 12
        else -> initialHour
    }
    val hourState = rememberLazyListState(initial12Hour - 1)
    val minuteState = rememberLazyListState(initialMinute)
    val amPmState = rememberLazyListState(initialAmPm)
    val language = LocalAppLocale.current
    fun LazyListState.centerIndex(maxIndex: Int): Int {
        val layout = layoutInfo
        if (layout.visibleItemsInfo.isEmpty()) return 0
        val center = (layout.viewportStartOffset + layout.viewportEndOffset) / 2
        return layout.visibleItemsInfo.minByOrNull {
            val itemCenter = it.offset + it.size / 2
            abs(itemCenter - center)
        }?.index ?: 0
    }

    LaunchedEffect(Unit) {
        combine(
            snapshotFlow { hourState.centerIndex(hours.lastIndex) },
            snapshotFlow { minuteState.centerIndex(minutes.lastIndex) },
            snapshotFlow { amPmState.centerIndex(amPmItems.lastIndex) }
        ) { h, m, a -> Triple(h, m, a) }.distinctUntilChanged()
            .collect { (hIndex, mIndex, amPmIndex) ->
                val safeHourIndex = hIndex.coerceIn(hours.indices)
                val safeMinuteIndex = mIndex.coerceIn(minutes.indices)
                val safeAmPmIndex = amPmIndex.coerceIn(amPmItems.indices)

                val hour12 = hours[safeHourIndex]
                val minute = minutes[safeMinuteIndex]
                val isPm = safeAmPmIndex == 1

                val hour24 = when {
                    hour12 == 12 && !isPm -> 0
                    hour12 != 12 && isPm -> hour12 + 12
                    else -> hour12
                }
                onTimeChanged(hour24, minute)
            }
    }

    Box(
        modifier = modifier
            .height(202.dp)
            .widthIn(min = 224.dp, max = 448.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .width(244.dp)
                .height(28.dp)
                .background(
                    color = Theme.color.surfaces.surfaceHigh,
                    shape = RoundedCornerShape(8.dp)
                )
        )


        Row(
            modifier = Modifier.background(Color.Transparent),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            WheelColumn(
                items = hours.map {
                    "%02d".format(it).toLocalizedDigits(language)
                },
                state = hourState,
                width = 72.dp
            )

            WheelColumn(
                items = minutes.map {
                    "%02d".format(it).toLocalizedDigits(language)
                },
                state = minuteState,
                width = 72.dp
            )

            Spacer(modifier = Modifier.width(8.dp))

            WheelColumn(
                items = amPmItems,
                state = amPmState,
                width = 60.dp
            )
        }
    }
}

@Composable
fun TimeWheelPicker(
    modifier: Modifier = Modifier,
    initialHour: Int = 8,
    initialMinute: Int = 30,
    onTimeChanged: (Int, Int) -> Unit
) {
    val locale = LocalAppLocale.current

    val hours = (1..12).toList()
    val minutes = (0..59).toList()
    val am = localizedString(R.string.am)
    val pm = localizedString(R.string.pm)
    val amPmItems = remember(locale) {
        listOf(
            am,
            pm
        )
    }

    val initialAmPm = if (initialHour >= 12) 1 else 0
    val initial12Hour = when {
        initialHour == 0 -> 12
        initialHour > 12 -> initialHour - 12
        else -> initialHour
    }

    val hourState = rememberLazyListState(
        initialFirstVisibleItemIndex = initial12Hour - 1
    )

    val minuteState = rememberLazyListState(
        initialFirstVisibleItemIndex = initialMinute
    )

    val amPmState = rememberLazyListState(
        initialFirstVisibleItemIndex = initialAmPm
    )

    fun LazyListState.centerIndex(): Int {
        val layout = layoutInfo
        if (layout.visibleItemsInfo.isEmpty()) return 0

        val center = (layout.viewportStartOffset + layout.viewportEndOffset) / 2

        return layout.visibleItemsInfo.minByOrNull {
            val itemCenter = it.offset + it.size / 2
            abs(itemCenter - center)
        }?.index ?: 0
    }

    LaunchedEffect(hourState, minuteState, amPmState, locale) {
        combine(
            snapshotFlow { hourState.centerIndex() },
            snapshotFlow { minuteState.centerIndex() },
            snapshotFlow { amPmState.centerIndex() }
        ) { h, m, a -> Triple(h, m, a) }
            .distinctUntilChanged()
            .collect { (hIndex, mIndex, amPmIndex) ->

                val hour12 = hours[hIndex.coerceIn(hours.indices)]
                val minute = minutes[mIndex.coerceIn(minutes.indices)]
                val isPm = amPmIndex == 1

                val hour24 = when {
                    hour12 == 12 && !isPm -> 0
                    hour12 != 12 && isPm -> hour12 + 12
                    else -> hour12
                }

                onTimeChanged(hour24, minute)
            }
    }

    Box(
        modifier = modifier
            .height(202.dp)
            .widthIn(min = 224.dp, max = 448.dp),
        contentAlignment = Alignment.Center
    ) {

        Box(
            modifier = Modifier
                .width(244.dp)
                .height(28.dp)
                .background(
                    color = Theme.color.surfaces.surfaceHigh,
                    shape = RoundedCornerShape(8.dp)
                )
        )

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {

            WheelColumn(
                items = hours.map {
                    "%02d".format(it).toLocalizedDigits(locale)
                },
                state = hourState,
                width = 72.dp
            )

            WheelColumn(
                items = minutes.map {
                    "%02d".format(it).toLocalizedDigits(locale)
                },
                state = minuteState,
                width = 72.dp
            )

            Spacer(modifier = Modifier.width(8.dp))

            WheelColumn(
                items = amPmItems,
                state = amPmState,
                width = 60.dp
            )
        }
    }
}

@Composable
private fun WheelColumn(
    items: List<String>,
    state: LazyListState,
    width: Dp
) {
    val flingBehavior = rememberSnapFlingBehavior(state)

    val centerIndex by remember {
        derivedStateOf {
            val layout = state.layoutInfo
            if (layout.visibleItemsInfo.isEmpty()) return@derivedStateOf 0
            val center = (layout.viewportStartOffset + layout.viewportEndOffset) / 2
            layout.visibleItemsInfo.minByOrNull {
                abs((it.offset + it.size / 2) - center)
            }?.index ?: 0
        }
    }

    LazyColumn(
        state = state,
        flingBehavior = flingBehavior,
        modifier = Modifier
            .height(202.dp)
            .width(width)
            .background(Color.Transparent),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(vertical = 87.dp)
    ) {
        itemsIndexed(items) { index, item ->
            val isSelected = centerIndex == index

            val alpha by animateFloatAsState(
                targetValue = if (isSelected) 1f else 0.35f,
                label = ""
            )

            Box(
                modifier = Modifier
                    .height(28.dp)
                    .fillMaxWidth()
                    .background(Color.Transparent),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = item,
                    style = Theme.textStyle.label.medium,
                    color = Theme.color.primary.shadePrimary,
                    modifier = Modifier.alpha(alpha)
                )
            }
        }
    }
}

@Composable
private fun EmptyItem() {
    Box(
        modifier = Modifier
            .height(28.dp)
            .fillMaxWidth()
    )
}

@Preview(showBackground = true, heightDp = 250)
@Composable
fun TimeWheelPickerPreview() {
    TimeWheelPicker(
        initialHour = 10,
        initialMinute = 45,
        onTimeChanged = { _, _ -> }
    )
}

@Preview(showBackground = true, heightDp = 250)
@Composable
fun WheelColumnPreview() {
    val items = listOf("01", "02", "03", "04", "05")
    val state = rememberLazyListState(initialFirstVisibleItemIndex = 0)

    WheelColumn(
        items = items,
        state = state,
        width = 80.dp
    )
}

@Preview(showBackground = true)
@Composable
fun EmptyItemPreview() {
    EmptyItem()
}