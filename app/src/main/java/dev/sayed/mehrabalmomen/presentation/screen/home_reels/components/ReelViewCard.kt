package dev.sayed.mehrabalmomen.presentation.screen.home_reels.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.theme.MehrabTheme
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.screen.reels.components.HeartIcon

@Composable
fun ReelViewCard(
    sheikhAvatarUrl: String,
    sheikhName: String,
    surahName: String,
    thumbnailUrl: String,
    ayah: String,
    isLiked: Boolean,
    likesCount: Int,
    sharesCount: Int,
    onLikeClick: () -> Unit,
    onShareClick: () -> Unit,
    modifier: Modifier = Modifier
) {
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
            onThumbnailClick = onLikeClick
        )

        ReelFooter(
            ayah = ayah,
            isLiked = isLiked,
            likesCount = likesCount,
            sharesCount = sharesCount,
            onLikeClick = onLikeClick,
            onShareClick = onShareClick
        )
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
private fun ReelFooter(ayah: String,
                       isLiked : Boolean,
                       likesCount: Int,
                       sharesCount: Int,
                       onLikeClick: () -> Unit,
                       onShareClick: () -> Unit,
                       modifier: Modifier = Modifier) {

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
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            ReelAction(
                text = pluralStringResource(
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
            ReelAction(
                text = pluralStringResource(
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

@Preview(
    showBackground = true,
    backgroundColor = 0xFF000000
)
@Composable
private fun ReelViewCardPreview() {
    data class ReelPreviewData(
        val sheikhAvatarUrl: String,
        val sheikhName: String,
        val surahName: String,
        val thumbnailUrl: String,
        val ayah: String,
        val isLiked: Boolean,
        val likesCount: Int,
        val sharesCount: Int,
    )

    val previewItems = listOf(
        ReelPreviewData(
            sheikhAvatarUrl = "https://i.pravatar.cc/150?img=3",
            sheikhName = "Mishary Rashid Al-Afasy",
            surahName = "Surah Al-Baqarah • Ayah 286",
            thumbnailUrl = "https://images.unsplash.com/photo-1564769662533-4f00a87b4056?w=600",
            ayah = "Allah does not burden a soul beyond that it can bear.",
            isLiked = true,
            likesCount = 1200,
            sharesCount = 340,
        ),
        ReelPreviewData(
            sheikhAvatarUrl = "https://i.pravatar.cc/150?img=12",
            sheikhName = "Abdul Rahman Al-Sudais",
            surahName = "Surah Al-Fatiha • Ayah 1",
            thumbnailUrl = "https://images.unsplash.com/photo-1542816417-0983c9c9ad53?w=600",
            ayah = "In the name of Allah, the Most Gracious, the Most Merciful.",
            isLiked = false,
            likesCount = 4300,
            sharesCount = 980,
        ),
        ReelPreviewData(
            sheikhAvatarUrl = "https://i.pravatar.cc/150?img=22",
            sheikhName = "Maher Al Mueaqly",
            surahName = "Surah Al-Imran • Ayah 103",
            thumbnailUrl = "https://images.unsplash.com/photo-1519817650390-64a93db51149?w=600",
            ayah = "Hold firmly to the rope of Allah all together and do not become divided.",
            isLiked = true,
            likesCount = 870,
            sharesCount = 210,
        ),
        ReelPreviewData(
            sheikhAvatarUrl = "https://i.pravatar.cc/150?img=8",
            sheikhName = "Saud Al-Shuraim",
            surahName = "Surah Yasin • Ayah 12",
            thumbnailUrl = "https://images.unsplash.com/photo-1533000971552-6a962ff0b9f9?w=600",
            ayah = "Indeed it is We who give life to the dead and record what they have put forth.",
            isLiked = false,
            likesCount = 2100,
            sharesCount = 560,
        ),
    )

    MehrabTheme(isDarkTheme = true) {
        LazyColumn(
            modifier = Modifier.statusBarsPadding().navigationBarsPadding()
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            items(previewItems) { item ->
                ReelViewCard(
                    sheikhAvatarUrl = item.sheikhAvatarUrl,
                    sheikhName = item.sheikhName,
                    surahName = item.surahName,
                    thumbnailUrl = item.thumbnailUrl,
                    ayah = item.ayah,
                    isLiked = item.isLiked,
                    likesCount = item.likesCount,
                    sharesCount = item.sharesCount,
                    onLikeClick = {},
                    onShareClick = {}
                )
            }
        }
    }
}