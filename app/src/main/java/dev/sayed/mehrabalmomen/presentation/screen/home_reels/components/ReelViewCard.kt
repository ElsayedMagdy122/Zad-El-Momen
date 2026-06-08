package dev.sayed.mehrabalmomen.presentation.screen.home_reels.components

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.base.localizedPlural
import dev.sayed.mehrabalmomen.presentation.screen.home_reels.HomeReelsUiState
import dev.sayed.mehrabalmomen.presentation.screen.reels.components.HeartIcon

@Composable
fun ReelViewCard(
    reelPreview: HomeReelsUiState.ReelPreviewUiState,
    onLikeClick: (Int) -> Unit,
    onShareClick: (Int) -> Unit,
    onThumbnailClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    with(reelPreview) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .background(
                    Theme.color.primary.onPrimary,
                    RoundedCornerShape(16.dp)
                )
        ) {
            ReelHeader(
                sheikhAvatarUrl = sheikhAvatarUrl,
                sheikhName = sheikhName,
                surahName = surahName
            )

            ReelContent(
                thumbnailUrl = thumbnailUrl,
                onThumbnailClick = { onThumbnailClick(id) }
            )

            ReelFooter(
                ayah = ayah,
                isLiked = isLikedOptimistic,
                likesCount = likesCountOptimistic,
                sharesCount = sharesCount,
                onLikeClick = { onLikeClick(id) },
                onShareClick = { onShareClick(id) },
                isSharing = isSharing,
                downloadPercentage = downloadPercentage
            )
        }
    }
}

@Composable
private fun ReelHeader(
    sheikhAvatarUrl: String,
    sheikhName: String,
    surahName: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier
            .fillMaxWidth()
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AsyncImage(
            model = sheikhAvatarUrl,
            contentDescription = null,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = sheikhName,
                style = Theme.textStyle.label.medium,
                color = Theme.color.primary.shadePrimary,
                maxLines = 1,
            )
            Text(
                text = surahName,
                style = Theme.textStyle.label.small,
                color = Theme.color.secondary.shadeSecondary
            )
        }
    }
}

@Composable
private fun ReelContent(
    thumbnailUrl: String,
    onThumbnailClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            thumbnailUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp)
                .clickable(interactionSource = null, onClick = onThumbnailClick),
            alignment = Alignment.Center,
        )

        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Theme.color.primary.onPrimaryHint),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_play),
                contentDescription = null,
                tint = Theme.color.primary.onPrimary,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

@Composable
private fun ReelFooter(
    ayah: String,
    isLiked: Boolean,
    likesCount: Int,
    sharesCount: Int,
    isSharing: Boolean,
    downloadPercentage: Int,
    onLikeClick: () -> Unit,
    onShareClick: () -> Unit,
    modifier: Modifier = Modifier,
) {

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = ayah,
            style = Theme.textStyle.body.small,
            color = Theme.color.primary.shadePrimary
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ReelAction(
                text = localizedPlural(
                    R.plurals.likes_suffix,
                    likesCount,
                    likesCount
                ),
                icon = {
                    HeartIcon(
                        isLiked = isLiked,
                        onClick = onLikeClick,
                        iconSize = 24.dp
                    )
                }
            )
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
                } else

                    ReelAction(
                        text = localizedPlural(
                            R.plurals.shares_suffix,
                            sharesCount,
                            sharesCount,
                        ),
                        icon = {
                            Icon(
                                painter = painterResource(R.drawable.ic_share),
                                contentDescription = null,
                                tint = Theme.color.semantic.textDisabled,
                                modifier = Modifier
                                    .size(24.dp)
                                    .clickable(
                                        interactionSource = null,
                                        indication = null,
                                        onClick = onShareClick
                                    )
                            )
                        }
                    )
            }
        }
    }
}

@Composable
private fun ReelAction(
    text: String,
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {

        icon()

        Text(
            text = text,
            style = Theme.textStyle.body.small,
            color = Theme.color.semantic.textDisabled
        )
    }
}