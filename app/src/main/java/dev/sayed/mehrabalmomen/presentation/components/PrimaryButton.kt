package dev.sayed.mehrabalmomen.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.design_system.theme.Theme

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    isEnabled: Boolean = true,
    enabledContainerColor: Color = Theme.color.primary.primary,
    disabledContainerColor: Color = Theme.color.semantic.disabled,
    enabledTextColor: Color = Theme.color.primary.onPrimary,
    disabledTextColor: Color = Theme.color.semantic.textDisabled,
) {
    val containerColor by animateColorAsState(
        targetValue = if (isEnabled) enabledContainerColor else disabledContainerColor,
        label = "ContainerColorAnimation"
    )

    val textColor by animateColorAsState(
        targetValue = if (isEnabled) enabledTextColor else disabledTextColor,
        label = "TextColorAnimation"
    )
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(containerColor)
            .clickable(onClick = onClick)
            .clickable(enabled = isEnabled && !isLoading, onClick = onClick)
            .padding(16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        AnimatedContent(
            targetState = text,
            label = "TextChangeAnimation"
        ) { newText ->
            Text(
                text = newText,
                color = textColor,
                style = Theme.textStyle.label.medium
            )
        }
        Crossfade(targetState = isLoading) { loading ->
            if (loading) {
                Spacer(modifier = Modifier.padding(start = 12.dp))
                AnimatedLoadingIndicator(
                    size = 20.dp
                )
            }
        }
    }
}

@Preview
@Composable
private fun PrimaryButtonPreview() {
    PrimaryButton(text = "Primary Button", onClick = {})
    PrimaryButton(text = "Primary Button", onClick = {}, isLoading = true, isEnabled = false)
}