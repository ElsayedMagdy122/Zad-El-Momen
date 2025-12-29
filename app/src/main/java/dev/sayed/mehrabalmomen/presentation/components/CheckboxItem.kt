package dev.sayed.mehrabalmomen.presentation.components

import CheckboxTick
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.theme.MehrabTheme
import dev.sayed.mehrabalmomen.design_system.theme.Theme

@Composable
fun CheckboxItem(
    text: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    description: String? = null,
    icon: Painter? = null,
    tintColor: Color= Theme.color.primary.primary,
    titleColor: Color = Theme.color.primary.primary,
    backgroundColor: Color = Theme.color.surfaces.surfaceLow
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .fillMaxWidth()
            .clickable { onCheckedChange(!isChecked) }
            .padding(vertical = 12.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AnimatedVisibility(visible = icon != null) {
            icon?.let {
                Icon(
                    modifier = Modifier
                        .padding(start = 12.dp)
                        .align(Alignment.CenterVertically),
                    painter = it,
                    tint = tintColor,
                    contentDescription = null

                )
            }
        }

        CheckboxText(
            text = text,
            description = description,
            titleColor = titleColor,
            modifier = Modifier.weight(1f)
        )
        CheckboxTick(
            isChecked = isChecked,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
private fun CheckboxText(
    text: String,
    description: String?,
    titleColor: Color,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = text,
            color = titleColor,
            style = Theme.textStyle.title.small
        )
        description?.let {
            Text(
                modifier = Modifier.padding(top = 4.dp),
                text = it,
                color = Theme.color.secondary.shadeSecondary,
                style = Theme.textStyle.body.small
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CheckboxItemPreview() {
    var checked by remember { mutableStateOf(false) }

    MehrabTheme(isDarkTheme = false) {
        Column(
            modifier = Modifier
                .background(Theme.color.surfaces.surface)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CheckboxItem(
                icon = painterResource(R.drawable.ic_theme),
                text = "Tick item",
                isChecked = checked,
                onCheckedChange = { checked = it },
            )

            CheckboxItem(
                text = "Switch item",
                description = "Checkbox with switch",
                isChecked = !checked,
                onCheckedChange = { checked = it },
            )
        }
    }
}