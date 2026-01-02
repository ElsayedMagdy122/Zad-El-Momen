package dev.sayed.mehrabalmomen.design_system.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.design_system.theme.MehrabTheme
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import kotlinx.coroutines.delay

@Composable
fun PrimaryToast(
    data: ToastDetails,
    modifier: Modifier = Modifier,
    durationMillis: Long = 3000L,
    isSuccess: Boolean = true
) {
    var visible by remember { mutableStateOf(true) }

    LaunchedEffect(data) {
        visible = true
        delay(durationMillis)
        visible = false
    }
    val iconTint = if (isSuccess) Theme.color.semantic.success else Theme.color.semantic.error
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .widthIn(
                    min = 320.dp, max = 400.dp
                )
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
                    text = data.title,
                    color = Theme.color.primary.shadePrimary,
                    style = Theme.textStyle.label.medium
                )
                Text(
                    text = data.message,
                    color = Theme.color.secondary.shadeSecondary,
                    style = Theme.textStyle.body.small
                )
            }
        }
    }
}

data class ToastDetails(
    val title: String,
    val message: String,
    val icon: Int
)

@Preview
@Composable
private fun ToastPreview() {
    MehrabTheme(isDarkTheme = false) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            PrimaryToast(
                data = ToastDetails(
                    title = "Success",
                    message = "message description",
                    icon = dev.sayed.mehrabalmomen.R.drawable.ic_check_circle
                )
            )
            PrimaryToast(
                data = ToastDetails(
                    title = "Error",
                    message = "message description",
                    icon = dev.sayed.mehrabalmomen.R.drawable.ic_close_circle
                ),
                isSuccess = false
            )
        }
    }
}