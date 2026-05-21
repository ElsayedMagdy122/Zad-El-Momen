package dev.sayed.mehrabalmomen.presentation.screen.reels.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.design_system.theme.Theme

@Composable
fun ReelsAyah(ayah : String, modifier: Modifier = Modifier) {
    Box(
        modifier
            .fillMaxWidth()
            .background(
                color = Theme.color.surfaces.surface.copy(.3f),
                shape = RoundedCornerShape(12.dp)
            )
            .border(
                width = 1.dp,
                color = Theme.color.primary.primary.copy(.1f),
                shape = RoundedCornerShape(12.dp)
            ).padding(16.dp)
    )
    {
        Text(
            text = ayah,
            style = Theme.textStyle.body.medium,
            color = Theme.color.secondary.secondaryText,

        )
    }


}