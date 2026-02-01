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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
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
    placeholder: String = "",
    onBackClick: () -> Unit,
    isSearchMode: Boolean = false,
    searchText: String = "",
    onSearchTextChange: (String) -> Unit = {},
    onSearchClose: () -> Unit = {},
    titleColor: Color = Theme.color.semantic.shadeTertiary,
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

        Box(
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp)
        ) {
            if (isSearchMode) {
                SearchTextField(
                    value = searchText,
                    onValueChange = onSearchTextChange,
                    placeholder = placeholder,
                    onClearClick = onSearchClose,
                    isRtl = isRtl
                )
            } else {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = title,
                    textAlign = TextAlign.Start,
                    color = titleColor,
                    style = Theme.textStyle.title.medium
                )
            }
        }

        if (!isSearchMode) {
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
}

@Composable
private fun SearchTextField(
    value: String,
    onValueChange: (String) -> Unit,
    onClearClick: () -> Unit,
    placeholder: String,
    isRtl: Boolean
) {
    BasicTextField(
        value = value,
        singleLine = true,
        onValueChange = onValueChange,
        textStyle = Theme.textStyle.body.medium.copy(color = Theme.color.primary.shadePrimary),
        cursorBrush = SolidColor(Theme.color.primary.primary),
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Theme.color.surfaces.surfaceLow)
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    modifier = Modifier
                        .size(24.dp)
                        .padding(end = 8.dp)
                        .graphicsLayer {
                            scaleX = if (isRtl) -1f else 1f
                        },
                    painter = painterResource(R.drawable.ic_search),
                    contentDescription = null,
                    tint = Theme.color.secondary.shadeSecondary
                )
                if (value.isEmpty()) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = placeholder,
                        maxLines = 1,
                        style = Theme.textStyle.label.medium,
                        color = Theme.color.primary.shadePrimary
                    )
                }
                innerTextField()
                Spacer(modifier = Modifier.weight(1f))
                if (value.isNotEmpty()) {
                    Icon(
                        modifier = Modifier
                            .size(20.dp)
                            .clickable { onClearClick() },
                        painter = painterResource(R.drawable.ic_cancel_circle),
                        contentDescription = "Clear",
                        tint = Theme.color.primary.primary
                    )
                }
            }
        }
    )
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
        title = "Al-Baqarah",
        onBackClick = {},
        actions = listOf(
            AppBarAction(
                icon = painterResource(R.drawable.ic_search),
                onClick = {},

                ),
            AppBarAction(
                icon = painterResource(R.drawable.ic_all_bookmark),
                onClick = {},
            )
        ),
        isSearchMode = false,
        searchText = "vddg",
        placeholder = ""
    )
}