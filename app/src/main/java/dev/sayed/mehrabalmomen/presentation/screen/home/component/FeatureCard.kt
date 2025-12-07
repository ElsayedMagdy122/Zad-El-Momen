package dev.sayed.mehrabalmomen.presentation.screen.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.theme.Theme

@Composable
fun FeatureCard(
    icon: Painter,
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Theme.color.surfaces.surfaceLow)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {

        Icon(
            painter = painterResource(R.drawable.mosque_column),
            contentDescription = null,
            modifier = Modifier
                .fillMaxHeight()
                .padding(end = 10.dp)
                .align(Alignment.CenterEnd)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentHeight()
                .padding(start = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.Start,
        ) {
            CardIcon(icon = icon, contentDescription = title)
            Text(
                text = title,
                style = Theme.textStyle.title.small,
                color = Theme.color.primary.shadePrimary,
            )
        }
    }
}

@Composable
private fun CardIcon(
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
            .size(24.dp)
    )
}
