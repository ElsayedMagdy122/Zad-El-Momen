package dev.sayed.mehrabalmomen.design_system.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.design_system.theme.MehrabTheme
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.designsystem.generated.resources.Res
import dev.sayed.mehrabalmomen.designsystem.generated.resources.check
import org.jetbrains.compose.resources.painterResource

@Composable
fun CheckboxTick(
    isChecked: Boolean,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isChecked)
            Theme.color.primary.primary
        else
            Theme.color.surfaces.surfaceHigh,
        animationSpec = tween(300)
    )

    Box(
        modifier = modifier
            .size(24.dp)
            .clip(CircleShape)
            .background(backgroundColor)
            .border(
                width = if (isChecked) 0.dp else 1.dp,
                color = Theme.color.surfaces.surface,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = isChecked,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Icon(
                painter = painterResource(Res.drawable.check),
                contentDescription = null,
                tint = Theme.color.primary.onPrimary,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Preview
@Composable
private fun CheckboxTickPreview() {
    MehrabTheme(isDarkTheme = false) {
        Column(
            modifier = Modifier
                .background(Theme.color.surfaces.surface)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CheckboxTick(isChecked = true)
            CheckboxTick(isChecked = false)
        }
    }
}
