package dev.sayed.mehrabalmomen.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.theme.Theme

@Composable
fun CardIcon(
    icon: Painter,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    Icon(
        painter = icon,
        contentDescription = contentDescription,
        tint = Theme.color.primary.primary,
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Theme.color.surfaces.surfaceHigh)
            .padding(8.dp)
    )
}

@Preview
@Composable
private fun CardIconPreview() {
    CardIcon(
        modifier = Modifier.size(40.dp),
        icon = painterResource(id = R.drawable.kaaba_01),
        contentDescription = "Mosque Column"
    )
}