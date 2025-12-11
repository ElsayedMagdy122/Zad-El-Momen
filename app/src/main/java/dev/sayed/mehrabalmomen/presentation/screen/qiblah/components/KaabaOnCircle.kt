package dev.sayed.mehrabalmomen.presentation.screen.qiblah.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.theme.Theme

@Composable
fun KaabaOnCircle(
    directionDegrees: Float,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .border(
                width = 5.dp,
                color = Theme.color.secondary.secondaryText,
                shape = CircleShape
            )
            .background(Theme.color.surfaces.surface),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.qibla_compass),
            contentDescription = null,
            modifier = Modifier
                .padding(6.dp)
                .size(200.dp)
                .graphicsLayer {
                    rotationZ = directionDegrees
                },
            tint = Theme.color.primary.primary
        )
    }
}