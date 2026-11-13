package dev.sayed.mehrabalmomen.design_system.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.designsystem.resources.Res
import dev.sayed.mehrabalmomen.designsystem.resources.ic_check_circle
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun PrimaryToast(
    data: ToastDetails,
    durationMs: Long = 3000L,
    modifier: Modifier = Modifier,
    isSuccess: Boolean = true
) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(data) {
        isVisible = true
        delay(durationMs)
        isVisible = false
    }

    val iconTint = if (isSuccess) Theme.color.primary.shadePrimary else Theme.color.semantic.error

    val titleText = data.titleRes?.let { stringResource(it) } ?: data.title
    val messageText = data.messageRes?.let { stringResource(it) } ?: data.message

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn() + slideInVertically(initialOffsetY = { -it }),
        exit = fadeOut() + slideOutVertically(targetOffsetY = { -it }),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .widthIn(min = 280.dp, max = 360.dp)
                .background(
                    color = Theme.color.surfaces.surfaceLow,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(data.icon),
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(28.dp)
            )

            Spacer(Modifier.size(8.dp))

            Column {
                Text(
                    text = titleText,
                    color = Theme.color.primary.shadePrimary,
                    style = Theme.textStyle.label.medium
                )
                Text(
                    text = messageText,
                    color = Theme.color.secondary.shadeSecondary,
                    style = Theme.textStyle.body.small
                )
            }
        }
    }
}

data class ToastDetails(
    val title: String = "",
    val message: String = "",
    val titleRes: StringResource? = null,
    val messageRes: StringResource? = null,
    val icon: DrawableResource = Res.drawable.ic_check_circle
) {
    constructor(
        title: StringResource,
        message: StringResource,
        icon: DrawableResource = Res.drawable.ic_check_circle
    ) : this(
        title = "",
        message = "",
        titleRes = title,
        messageRes = message,
        icon = icon
    )
}
