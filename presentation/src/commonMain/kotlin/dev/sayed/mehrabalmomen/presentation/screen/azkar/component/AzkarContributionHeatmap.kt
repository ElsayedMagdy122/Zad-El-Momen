package dev.sayed.mehrabalmomen.presentation.screen.azkar.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.base.localizedString
import dev.sayed.mehrabalmomen.presentation.screen.azkar.AzkarTimeFilter
import java.time.LocalDate

@Composable
fun AzkarContributionHeatmap(
    historyMap: Map<String, Int>,
    selectedFilter: AzkarTimeFilter,
    onSelectFilter: (AzkarTimeFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Theme.color.surfaces.surfaceLow)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header & Filter Chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = localizedString(R.string.azkar_activity_log),
                style = Theme.textStyle.title.small,
                color = Theme.color.primary.primary,
                fontWeight = FontWeight.Bold
            )

            // Filter Chips Row
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                FilterChip(
                    text = localizedString(R.string.azkar_filter_day),
                    selected = selectedFilter == AzkarTimeFilter.DAY,
                    onClick = { onSelectFilter(AzkarTimeFilter.DAY) }
                )
                FilterChip(
                    text = localizedString(R.string.azkar_filter_week),
                    selected = selectedFilter == AzkarTimeFilter.WEEK,
                    onClick = { onSelectFilter(AzkarTimeFilter.WEEK) }
                )
                FilterChip(
                    text = localizedString(R.string.azkar_filter_month),
                    selected = selectedFilter == AzkarTimeFilter.MONTH,
                    onClick = { onSelectFilter(AzkarTimeFilter.MONTH) }
                )
                FilterChip(
                    text = localizedString(R.string.azkar_filter_year),
                    selected = selectedFilter == AzkarTimeFilter.YEAR,
                    onClick = { onSelectFilter(AzkarTimeFilter.YEAR) }
                )
            }
        }

        // Determine days count based on filter
        val totalDays = when (selectedFilter) {
            AzkarTimeFilter.DAY -> 1
            AzkarTimeFilter.WEEK -> 7
            AzkarTimeFilter.MONTH -> 30
            AzkarTimeFilter.YEAR -> 364 // 52 weeks * 7 days for true GitHub contribution grid
        }

        val today = LocalDate.now()
        val dateList = (0 until totalDays).map { i ->
            today.minusDays((totalDays - 1 - i).toLong())
        }

        // Group into columns (weeks of 7 days)
        val weeks = dateList.chunked(7)

        // Horizontal scrollable container for true GitHub style matrix
        val scrollState = rememberScrollState()
        LaunchedEffect(selectedFilter) {
            scrollState.scrollTo(scrollState.maxValue)
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (week in weeks) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        for (date in week) {
                            val dateStr = date.toString()
                            val count = historyMap[dateStr] ?: 0
                            HeatmapSquare(count = count)
                        }
                    }
                }
            }
        }

        // Legend
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = localizedString(R.string.azkar_less), fontSize = 10.sp, color = Theme.color.secondary.shadeSecondary)
            Spacer(modifier = Modifier.width(4.dp))
            Box(modifier = Modifier.size(10.dp).clip(RoundedCornerShape(2.dp)).background(Theme.color.surfaces.surfaceHigh))
            Spacer(modifier = Modifier.width(2.dp))
            Box(modifier = Modifier.size(10.dp).clip(RoundedCornerShape(2.dp)).background(Theme.color.primary.primary.copy(alpha = 0.3f)))
            Spacer(modifier = Modifier.width(2.dp))
            Box(modifier = Modifier.size(10.dp).clip(RoundedCornerShape(2.dp)).background(Theme.color.primary.primary.copy(alpha = 0.7f)))
            Spacer(modifier = Modifier.width(2.dp))
            Box(modifier = Modifier.size(10.dp).clip(RoundedCornerShape(2.dp)).background(Theme.color.primary.primary))
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = localizedString(R.string.azkar_more), fontSize = 10.sp, color = Theme.color.secondary.shadeSecondary)
        }
    }
}

@Composable
fun FilterChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (selected) Theme.color.primary.primary else Theme.color.surfaces.surfaceHigh)
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = if (selected) Theme.color.surfaces.surface else Theme.color.primary.primary
        )
    }
}

@Composable
fun HeatmapSquare(
    count: Int
) {
    val backgroundColor = when {
        count == 0 -> Theme.color.surfaces.surfaceHigh
        count < 3 -> Theme.color.primary.primary.copy(alpha = 0.35f)
        count < 6 -> Theme.color.primary.primary.copy(alpha = 0.7f)
        else -> Theme.color.primary.primary
    }

    Box(
        modifier = Modifier
            .size(14.dp)
            .clip(RoundedCornerShape(3.dp))
            .background(backgroundColor)
    )
}
