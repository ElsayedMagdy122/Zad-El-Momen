package dev.sayed.mehrabalmomen.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import zadelmomen.presentation.generated.resources.Res
import zadelmomen.presentation.generated.resources.al_tafseer
import zadelmomen.presentation.generated.resources.bookmark
import zadelmomen.presentation.generated.resources.copy
import zadelmomen.presentation.generated.resources.ic_copy_01
import zadelmomen.presentation.generated.resources.ic_play
import zadelmomen.presentation.generated.resources.ic_quran_02
import zadelmomen.presentation.generated.resources.ic_tafseer
import zadelmomen.presentation.generated.resources.listen

enum class AyahAction {
    BOOKMARK, COPY, TAFSEER, LISTEN
}

@Composable
fun AyahActions(
    onActionClick: (AyahAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val actions = listOf(
        ActionItem(
            icon = painterResource(Res.drawable.ic_play),
            text = stringResource(Res.string.listen),
            action = AyahAction.LISTEN
        ),
        ActionItem(
            icon = painterResource(Res.drawable.ic_quran_02),
            text = stringResource(Res.string.bookmark),
            action = AyahAction.BOOKMARK
        ),
        ActionItem(
            icon = painterResource(Res.drawable.ic_copy_01),
            text = stringResource(Res.string.copy),
            action = AyahAction.COPY
        ),
        ActionItem(
            icon = painterResource(Res.drawable.ic_tafseer),
            text = stringResource(Res.string.al_tafseer),
            action = AyahAction.TAFSEER
        )
    )

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(Theme.color.surfaces.surfaceHigh)
            .padding(vertical = 12.dp, horizontal = 16.dp)
            .widthIn(max = 340.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        actions.forEach { item ->
            ActionIconButton(
                icon = item.icon,
                text = item.text,
                onClick = { onActionClick(item.action) }
            )
        }
    }
}

@Composable
private fun ActionIconButton(
    icon: Painter,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(
                onClick = onClick,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            )
            .padding(horizontal = 12.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            modifier = Modifier.size(24.dp),
            painter = icon,
            contentDescription = text,
            tint = Theme.color.primary.primary
        )

        Text(
            modifier = Modifier.padding(top = 4.dp),
            text = text,
            style = Theme.textStyle.label.small,
            color = Theme.color.primary.primary
        )
    }
}

private data class ActionItem(
    val icon: Painter,
    val text: String,
    val action: AyahAction
)
