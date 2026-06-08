package dev.sayed.mehrabalmomen.presentation.screen.reels.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.theme.Theme

@Composable
fun SheikhImage(sheikhAvatarUrl : String,modifier: Modifier = Modifier) {
    if (sheikhAvatarUrl.isEmpty())
        Icon(
            painter = painterResource(R.drawable.ic_moazen),
            tint = Theme.color.primary.primary,
            modifier = modifier
                .size(40.dp)
                .clip(CircleShape).background(Theme.color.primary.primary.copy(alpha = 0.1f)),
            contentDescription = null
        )
    else
        AsyncImage(
            model = sheikhAvatarUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = modifier
                .size(40.dp)
                .clip(CircleShape)
                .shadow(
                    1.dp,
                    CircleShape,
                    true,
                    Theme.color.secondary
                        .secondary,
                    Theme.color.secondary
                        .secondary
                ),
        )
}