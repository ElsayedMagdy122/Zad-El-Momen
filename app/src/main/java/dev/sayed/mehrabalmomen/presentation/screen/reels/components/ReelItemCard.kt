package dev.sayed.mehrabalmomen.presentation.screen.reels.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.media3.exoplayer.ExoPlayer
import coil.compose.AsyncImage
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.screen.reels.ReelItemUiState
import dev.sayed.mehrabalmomen.presentation.screen.reels.ReelsInteractionListener

@Composable
fun ReelItemCard(
    item: ReelItemUiState,
    player: ExoPlayer?,
    isActive: Boolean,
    isReady: Boolean,
    interactionListener: ReelsInteractionListener,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {

        if (player != null && isReady) {
            ReelVideoPlayer(
                player = player,
                isActive = isActive,
                modifier = Modifier.fillMaxSize(),
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.5f)
                .zIndex(10f)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f)),
                    )
                )
        )

        // ── Right-side action column ───────────────────────────────────────────
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
                .padding(end = 16.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            LikeButton(
                count = item.likesCountOptimistic,
                isLiked = item.isLikedOptimistic,
                onClick = { interactionListener.onLikeReelClicked(item.id) },
            )
            ShareButton(
                sharesCount = item.sharesCount,
                isSharing = item.isSharing,
                downloadPercentage = item.downloadPercentage,
                onClick = { interactionListener.onShareClicked(item.id) },
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 16.dp, end = 80.dp, bottom = 24.dp)
                .navigationBarsPadding(),
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                if (item.sheikhAvatarUrl.isEmpty())
                    Icon(
                        painter = painterResource(R.drawable.ic_quran_dua),
                        tint = Theme.color.primary.primary,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape),
                        contentDescription = null
                    )
                else
                    AsyncImage(
                        model = item.sheikhAvatarUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
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
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = item
                            .sheikhName, style = Theme.textStyle.body.medium,
                        color = Theme
                            .color.primary.primary
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_quran),
                            contentDescription = "Quran",
                            tint = Theme.color.primary.primary.copy(.8f),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = item
                                .surah, style = Theme.textStyle.body.medium,
                            color = Theme
                                .color.primary.primary.copy(.8f)
                        )
                    }

                }
            }
            ReelsAyah(item.ayah, modifier = Modifier.padding(start = 16.dp, top = 12.dp))
        }
    }
}

fun formatCount(count: Int): String = when {
    count >= 1_000_000 -> "${count / 1_000_000}M"
    count >= 1_000 -> "${"%.1f".format(count / 1_000.0)}K"
    else -> count.toString()
}
