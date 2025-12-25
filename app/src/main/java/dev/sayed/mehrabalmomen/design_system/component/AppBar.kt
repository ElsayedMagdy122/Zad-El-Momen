package dev.sayed.mehrabalmomen.design_system.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.theme.Theme

@Composable
fun AppBar(
    title: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    isBackEnabled: Boolean = true,
) {
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding( vertical =  8.dp)
    ) {
        AnimatedVisibility(isBackEnabled) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Theme.color.surfaces.surfaceLow)
                    .clickable {
                        onBackClick()
                    }
                    .align(Alignment.CenterStart),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    modifier = Modifier
                        .size(12.dp)
                        .graphicsLayer {
                            scaleX = if (isRtl) -1f else 1f
                        },
                    painter = painterResource(id = R.drawable.ic_arrow_back),
                    contentDescription = null,
                    tint = Theme.color.primary.primary
                )
            }
        }

        Text(
            modifier = Modifier.align(Alignment.Center),
            text = title,
            color = Theme.color.primary.primary,
            style = Theme.textStyle.title.medium
        )
    }
}

@Preview
@Composable
private fun PrimaryAppBarPreview() {
    AppBar(title = "Prayer Times", onBackClick = {})
}