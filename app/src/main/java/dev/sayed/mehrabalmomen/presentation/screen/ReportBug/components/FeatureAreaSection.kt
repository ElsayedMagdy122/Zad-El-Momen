package dev.sayed.mehrabalmomen.presentation.screen.ReportBug.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.base.localizedString
import dev.sayed.mehrabalmomen.presentation.screen.ReportBug.FeatureArea

@Composable
fun FeatureAreaSection(
    selected: FeatureArea?,
    onSelect: (FeatureArea) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Theme.color.surfaces.surfaceLow)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SectionTitle(localizedString(R.string.feature_area))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            FeatureArea.entries.forEach { feature ->
                FeatureChip(
                    text = localizedString(feature.labelRes),
                    selected = feature == selected,
                    onClick = { onSelect(feature) }
                )
            }
        }
    }
}

@Composable
private fun FeatureChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor =
        if (selected) Theme.color.primary.primary
        else Theme.color.surfaces.surfaceLow

    val borderColor =
        if (selected) Color.Transparent
        else Theme.color.semantic.disabled

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(24.dp))
            .border(1.dp, borderColor, RoundedCornerShape(24.dp))
            .background(backgroundColor)
            .clickable { onClick() }
    ) {
        Text(
            text = text,
            color = if (selected)
                Theme.color.surfaces.surfaceLow
            else
                Theme.color.primary.primary,
            style = Theme.textStyle.label.medium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
        )
    }
}