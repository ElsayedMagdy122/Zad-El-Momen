package dev.sayed.mehrabalmomen.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.theme.Theme

enum class AyahAction {
    SEND, BOOKMARK, COPY
}

@Composable
fun AyahActions(
    onActionClick: (AyahAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val actions = listOf(
        ActionItem(
            icon = painterResource(R.drawable.ic_link_forward),
            text = "Send",
            action = AyahAction.SEND
        ),
        ActionItem(
            icon = painterResource(R.drawable.ic_all_bookmark),
            text = "Bookmark",
            action = AyahAction.BOOKMARK
        ),
        ActionItem(
            icon = painterResource(R.drawable.ic_copy_01),
            text = "Copy",
            action = AyahAction.COPY
        )
    )

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Theme.color.surfaces.surfaceLow)
            .padding(vertical = 12.dp, horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        actions.forEach { actionItem ->
            ActionButton(
                actionItem = actionItem,
                onClick = { onActionClick(actionItem.action) }
            )
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
            .widthIn(78.dp)
            .clickable(
                onClick = onClick,
                interactionSource = MutableInteractionSource(),
                indication = null,
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = actionItem.icon,
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