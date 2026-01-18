package dev.sayed.mehrabalmomen.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.theme.Theme

@Composable
fun QuranAppBar(
    title: String,
    onBackClick: () -> Unit,
    actions: List<AppBarAction> = emptyList(),
    modifier: Modifier = Modifier,
) {
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        IconContainer(
            isRtl = isRtl,
            icon = painterResource(R.drawable.ic_arrow_left_01),
            onClick = onBackClick
        )


        Text(
            modifier = Modifier
                .padding(start = 8.dp)
                .weight(1f),
            text = title,
            textAlign = TextAlign.Start,
            color = Theme.color.primary.shadePrimary,
            style = Theme.textStyle.title.medium
        )


        actions.forEach { action ->
            Spacer(modifier = Modifier.width(4.dp))
            IconContainer(
                isRtl = isRtl,
                icon = action.icon,
                onClick = action.onClick
            )
        }
    }
}

@Composable
private fun IconContainer(
    isRtl: Boolean,
    icon: Painter,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(40.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .background(Theme.color.surfaces.surfaceLow),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            modifier = Modifier
                .size(20.dp)
                .graphicsLayer {
                    scaleX = if (isRtl) -1f else 1f
                },
            painter = icon,
            contentDescription = null,
            tint = Theme.color.primary.primary
        )
    }
}

data class AppBarAction(
    val icon: Painter,
    val onClick: () -> Unit
)

@Preview
@Composable
private fun PrimaryAppBarPreview() {
    QuranAppBar(
        title = "Al-Baqarah", onBackClick = {}, actions = listOf(
            AppBarAction(
                icon = painterResource(R.drawable.ic_search),
                onClick = {},

                ),
            AppBarAction(
                icon = painterResource(R.drawable.ic_all_bookmark),
                onClick = {},
            )
        )
    )
}