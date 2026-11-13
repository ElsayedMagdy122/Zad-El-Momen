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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.designsystem.resources.Res
import dev.sayed.mehrabalmomen.designsystem.resources.al_tafseer
import dev.sayed.mehrabalmomen.designsystem.resources.bookmark
import dev.sayed.mehrabalmomen.designsystem.resources.copy
import dev.sayed.mehrabalmomen.designsystem.resources.ic_all_bookmark
import dev.sayed.mehrabalmomen.designsystem.resources.ic_copy_01
import dev.sayed.mehrabalmomen.designsystem.resources.ic_play
import dev.sayed.mehrabalmomen.designsystem.resources.ic_quran02
import dev.sayed.mehrabalmomen.designsystem.resources.listen
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

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
            icon = painterResource(Res.drawable.ic_all_bookmark),
            text = stringResource(Res.string.bookmark),
            action = AyahAction.BOOKMARK
        ),
        ActionItem(
            icon = painterResource(Res.drawable.ic_quran02),
            text = stringResource(Res.string.al_tafseer),
            action = AyahAction.TAFSEER
        ),
        ActionItem(
            icon = painterResource(Res.drawable.ic_copy_01),
            text = stringResource(Res.string.copy),
            action = AyahAction.COPY
        )
    )

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Theme.color.surfaces.surfaceLow)
            .padding(vertical = 12.dp, horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        actions.forEachIndexed { index, actionItem ->
            ActionButton(
                actionItem = actionItem,
                onClick = { onActionClick(actionItem.action) }
            )
            if (index < actions.lastIndex) {
                Box(
                    modifier = Modifier
                        .size(width = 1.dp, height = 20.dp)
                        .background(Color(0xFFEAECF0))
                )
            }
        }
    }
}

private data class ActionItem(
    val icon: Painter,
    val text: String,
    val action: AyahAction
)

@Composable
private fun ActionButton(
    actionItem: ActionItem,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .widthIn(min = 78.dp)
            .clickable(
                onClick = onClick,
                interactionSource = MutableInteractionSource(),
                indication = null,
            )
            .padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = actionItem.icon,
            modifier = Modifier.size(24.dp),
            tint = Theme.color.primary.primary,
            contentDescription = actionItem.text
        )
        Text(
            modifier = Modifier.padding(top = 2.dp),
            text = actionItem.text,
            color = Theme.color.primary.primary,
            style = Theme.textStyle.label.small
        )
    }
}
