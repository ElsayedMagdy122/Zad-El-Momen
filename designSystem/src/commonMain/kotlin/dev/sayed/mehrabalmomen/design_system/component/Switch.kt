package dev.sayed.mehrabalmomen.design_system.component

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.design_system.theme.MehrabTheme
import dev.sayed.mehrabalmomen.design_system.theme.Theme

@Composable
fun Switch(
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val trackWidth = 48.dp
    val trackHeight = 28.dp
    val thumbSize = 20.dp
    val padding = 4.dp

    val transition = updateTransition(isChecked, label = "Switch")

    val thumbOffset by transition.animateDp(label = "ThumbOffset") {
        if (it) trackWidth - thumbSize - padding else padding
    }

    val trackColor by transition.animateColor(label = "TrackColor") {
        if (it) Theme.color.primary.primary else Theme.color.semantic.disabled
    }

    Box(
        modifier = modifier
            .size(trackWidth, trackHeight)
            .clip(CircleShape)
            .background(trackColor)
            .clickable(onClick = { onCheckedChange(!isChecked) }),
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .offset(x = thumbOffset)
                .size(thumbSize)
                .background(
                    Theme.color.surfaces.surface,
                    CircleShape
                )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SwitchPreview() {
    MehrabTheme(isDarkTheme = false) {
        Column(
            modifier = Modifier
                .background(Theme.color.surfaces.surface)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Switch(isChecked = false, onCheckedChange = {})
            Switch(isChecked = true, onCheckedChange = {})
        }
    }
}