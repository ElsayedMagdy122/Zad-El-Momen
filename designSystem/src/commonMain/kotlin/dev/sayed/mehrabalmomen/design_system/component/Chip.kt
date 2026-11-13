package dev.sayed.mehrabalmomen.design_system.component

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.design_system.theme.MehrabTheme
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.domain.model.AppSettings

@Composable
fun Chip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    painter: Painter? = null,
    iconSize: Dp = 16.dp,
    isEnabled: Boolean = true,
    shape: Shape = RoundedCornerShape(100.dp),
    textStyle: TextStyle = Theme.textStyle.label.small,
    selectedContainerColor: Color = Theme.color.primary.primary,
    unselectedContainerColor: Color = Theme.color.surfaces.surfaceLow,
    selectedContentColor: Color = Theme.color.primary.onPrimary,
    unselectedContentColor: Color = Theme.color.secondary.shadeSecondary,
    disabledContainerColor: Color = Theme.color.semantic.disabled,
    disabledContentColor: Color = Theme.color.semantic.textDisabled,
) {
    val transition = updateTransition(targetState = isSelected, label = "ChipSelectionTransition")

    val containerColor by transition.animateColor(
        label = "ChipContainerColor",
        targetValueByState = { isCurrentSelected ->
            if (isCurrentSelected) selectedContainerColor
            else unselectedContainerColor
        }
    )

    val contentColor by transition.animateColor(
        label = "ChipContentColor",
        targetValueByState = { isCurrentSelected ->
            if (isCurrentSelected) selectedContentColor
            else unselectedContentColor
        }
    )

    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(shape)
            .then(
                if (isEnabled) Modifier.clickable(onClick = onClick)
                else Modifier
            )
            .background(if (isEnabled) containerColor else disabledContainerColor)
            .padding(
                vertical = 8.dp,
                horizontal = 12.dp
            )
    ) {
        painter?.let { iconPainter ->
            Icon(
                painter = iconPainter,
                modifier = Modifier.size(iconSize),
                contentDescription = null,
                tint = if (isEnabled) contentColor else disabledContentColor
            )
        }

        Text(
            text = text,
            style = textStyle,
            color = if (isEnabled) contentColor else disabledContentColor
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF2F4F7)
@Composable
private fun ChipPreview() {
    MehrabTheme(
        language = AppSettings.Language.ARABIC,
        isDarkTheme = false
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Chip(
                text = "غير محدد",
                isSelected = false,
                onClick = {}
            )

            Chip(
                text = "محدد",
                isSelected = true,
                onClick = {}
            )

            Chip(
                text = "معطّل",
                isSelected = true,
                isEnabled = false,
                onClick = {}
            )
        }
    }
}
