package dev.sayed.mehrabalmomen.presentation.screen.reels.components

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.theme.Theme

@Composable
fun ShareButton(
    sharesCount: Int,
    isSharing: Boolean,
    downloadPercentage: Int,
    onClick: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.clickable(enabled = !isSharing, onClick = onClick),
    ) {
        Box(modifier = Modifier.size(28.dp), contentAlignment = Alignment.Center) {
            Crossfade(targetState = isSharing, label = "shareIcon") { sharing ->
                if (sharing) {
                    Box(
                        modifier = Modifier.size(28.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(
                            progress = { downloadPercentage / 100f },
                            modifier = Modifier.size(28.dp),
                            color = Theme.color.primary.primary,
                            strokeWidth = 2.dp,
                        )
                        Text(
                            text = "$downloadPercentage",
                            color = Theme.color.primary.primary,
                            fontSize = 7.sp,
                        )
                    }
                } else {
                    Icon(
                        painter            = painterResource(R.drawable.share_01),
                        contentDescription = "Share",
                        tint               = Theme.color.semantic.shadeTertiary,
                        modifier           = Modifier.size(28.dp),
                    )
                }
            }
        }

        if (sharesCount > 0) {
            Text(
                text  = formatCount(sharesCount),
                color = Theme.color.primary.primary,
                style = Theme.textStyle.body.medium,
            )
        }
    }
}
