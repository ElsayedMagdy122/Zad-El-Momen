package dev.sayed.mehrabalmomen.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import org.jetbrains.compose.resources.painterResource
import zadelmomen.presentation.generated.resources.Res
import zadelmomen.presentation.generated.resources.shadow

@Composable
fun StateMessage(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    image: Painter? = null,
    showShadow: Boolean = false,
    imageModifier: Modifier = Modifier,
    action: @Composable (() -> Unit)? = null
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (image != null) {
            if (showShadow) {
                Box(contentAlignment = Alignment.Center) {
                    Image(
                        modifier = Modifier.align(Alignment.BottomCenter),
                        painter = painterResource(Res.drawable.shadow),
                        contentDescription = null
                    )
                    Image(
                        modifier = imageModifier.padding(bottom = 12.dp),
                        painter = image,
                        contentDescription = null
                    )
                }
            } else {
                Image(
                    modifier = imageModifier,
                    painter = image,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        Text(
            text = title,
            style = Theme.textStyle.title.small,
            color = Theme.color.primary.shadePrimary,
            textAlign = TextAlign.Center
        )

        if (!subtitle.isNullOrBlank()) {
            Text(
                text = subtitle,
                style = Theme.textStyle.body.small,
                textAlign = TextAlign.Center,
                color = Theme.color.secondary.shadeSecondary
            )
        }

        if (action != null) {
            Spacer(modifier = Modifier.height(16.dp))
            action()
        }
    }
}
