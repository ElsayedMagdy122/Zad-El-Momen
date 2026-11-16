package dev.sayed.mehrabalmomen.presentation.screen.reminders.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.style.TextAlign
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

private const val WHEEL_REPEAT_MULTIPLIER = 1000
private val WHEEL_COLUMN_WIDTH = 82.dp
private val WHEEL_ITEM_HEIGHT = 34.dp
private val WHEEL_TOTAL_HEIGHT = 238.dp // 7 visible items (3 above, 1 selected, 3 below)
private val WHEEL_VERTICAL_PADDING = 102.dp // 34.dp * 3 items

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
    val amPmItems = remember(locale, am, pm) {
        listOf(am, pm)
    }

    val initialAmPm = if (initialHour >= 12) 1 else 0
    val initial12Hour = when {
        initialHour == 0 -> 12
        initialHour > 12 -> initialHour - 12
        else -> initialHour
    }

    val hourStartIndex = remember(initial12Hour) {
        (WHEEL_REPEAT_MULTIPLIER / 2) * hours.size + (initial12Hour - 1)
    }
    val minuteStartIndex = remember(initialMinute) {
        (WHEEL_REPEAT_MULTIPLIER / 2) * minutes.size + initialMinute
    }

    val hourState = rememberLazyListState(
        initialFirstVisibleItemIndex = hourStartIndex
    )

    val minuteState = rememberLazyListState(
        initialFirstVisibleItemIndex = minuteStartIndex
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
                val hour12 = hours[(hIndex % hours.size).coerceIn(hours.indices)]
                val minute = minutes[(mIndex % minutes.size).coerceIn(minutes.indices)]
                val isPm = amPmIndex.coerceIn(0, 1) == 1

                val hour24 = when {
                    hour12 == 12 && !isPm -> 0
                    hour12 != 12 && isPm -> hour12 + 12
                    else -> hour12
                }

                onTimeChanged(hour24, minute)
            }
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Headers matching the Figma/design layout: الساعة, الدقيقة, الفترة
        Row(
            modifier = Modifier.padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = localizedString(R.string.hour),
                modifier = Modifier.width(WHEEL_COLUMN_WIDTH),
                textAlign = TextAlign.Center,
                style = Theme.textStyle.label.medium,
                color = Theme.color.primary.shadePrimary
            )

            Text(
                text = localizedString(R.string.minute),
                modifier = Modifier.width(WHEEL_COLUMN_WIDTH),
                textAlign = TextAlign.Center,
                style = Theme.textStyle.label.medium,
                color = Theme.color.primary.shadePrimary
            )

            Text(
                text = localizedString(R.string.period),
                modifier = Modifier.width(WHEEL_COLUMN_WIDTH),
                textAlign = TextAlign.Center,
                style = Theme.textStyle.label.medium,
                color = Theme.color.primary.shadePrimary
            )
        }

        Box(
            modifier = Modifier
                .height(WHEEL_TOTAL_HEIGHT)
                .widthIn(min = 246.dp, max = 448.dp),
            contentAlignment = Alignment.Center
        ) {
            // Selected item highlight container
            Box(
                modifier = Modifier
                    .width(WHEEL_COLUMN_WIDTH * 3)
                    .height(36.dp)
                    .background(
                        color = Theme.color.surfaces.surfaceHigh,
                        shape = RoundedCornerShape(10.dp)
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
                    width = WHEEL_COLUMN_WIDTH,
                    isLooping = true
                )

                WheelColumn(
                    items = minutes.map {
                        "%02d".format(it).toLocalizedDigits(locale)
                    },
                    state = minuteState,
                    width = WHEEL_COLUMN_WIDTH,
                    isLooping = true
                )

                WheelColumn(
                    items = amPmItems,
                    state = amPmState,
                    width = WHEEL_COLUMN_WIDTH,
                    isLooping = false
                )
            }
        }
    }
}

@Composable
private fun WheelColumn(
    items: List<String>,
    state: LazyListState,
    width: Dp,
    isLooping: Boolean
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

    val listSize = items.size
    val totalCount = if (isLooping) listSize * WHEEL_REPEAT_MULTIPLIER else listSize

    LazyColumn(
        state = state,
        flingBehavior = flingBehavior,
        modifier = Modifier
            .height(WHEEL_TOTAL_HEIGHT)
            .width(width)
            .background(Color.Transparent),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(vertical = WHEEL_VERTICAL_PADDING)
    ) {
        if (isLooping) {
            items(
                count = totalCount,
                key = { it }
            ) { index ->
                val actualIndex = index % listSize
                val item = items[actualIndex]
                WheelItem(
                    text = item,
                    distance = abs(centerIndex - index)
                )
            }
        } else {
            itemsIndexed(
                items = items,
                key = { index, _ -> index }
            ) { index, item ->
                WheelItem(
                    text = item,
                    distance = abs(centerIndex - index)
                )
            }
        }
    }
}

@Composable
private fun WheelItem(
    text: String,
    distance: Int
) {
    val targetAlpha = when (distance) {
        0 -> 1f
        1 -> 0.65f
        2 -> 0.32f
        3 -> 0.12f
        else -> 0f
    }

    val alpha by animateFloatAsState(
        targetValue = targetAlpha,
        label = "WheelAlpha"
    )

    Box(
        modifier = Modifier
            .height(WHEEL_ITEM_HEIGHT)
            .fillMaxWidth()
            .background(Color.Transparent),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = Theme.textStyle.label.medium,
            color = Theme.color.primary.shadePrimary,
            modifier = Modifier.alpha(alpha)
        )
    }
}

@Preview(showBackground = true, heightDp = 300)
@Composable
fun TimeWheelPickerPreview() {
    TimeWheelPicker(
        initialHour = 10,
        initialMinute = 45,
        onTimeChanged = { _, _ -> }
    )
}
