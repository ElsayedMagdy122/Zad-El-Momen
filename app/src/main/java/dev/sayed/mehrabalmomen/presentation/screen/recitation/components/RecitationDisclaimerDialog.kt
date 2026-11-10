package dev.sayed.mehrabalmomen.presentation.screen.recitation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.components.PrimaryDialog

/**
 * Design System compliant Disclaimer Dialog informing the user that AI recitation
 * is an assistive tool and does not replace learning from a qualified Quran teacher.
 */
@Composable
fun RecitationDisclaimerDialog(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    PrimaryDialog(
        onDismiss = onDismiss,
        backgroundColor = Theme.color.surfaces.surface,
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Theme.color.surfaces.surfaceHigh)
                    .clickable { onDismiss() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_close),
                    contentDescription = null,
                    tint = Theme.color.primary.primary,
                    modifier = Modifier.size(16.dp)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
                    .align(Alignment.TopCenter),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "تنبيه مهم بشأن التسميع",
                    textAlign = TextAlign.Center,
                    style = Theme.textStyle.title.medium,
                    color = Theme.color.primary.shadePrimary
                )

                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, start = 8.dp, end = 8.dp),
                    text = "يعتمد نموذج التسميع على التعرف على الكلمات القرآنية وترتيب الآيات لمساعدتك في مراجعة وتثبيت الحفظ فقط، ولا يقوم بالتدقيق على أحكام التجويد أو مخارج الحروف.\n\nهذه الميزة وسيلة مساعدة تقريبية ولا تُغني بأي حال عن التلقي والمشافهة والعرض على شيخ أو معلم متقن ومجاز.",
                    textAlign = TextAlign.Center,
                    style = Theme.textStyle.body.small,
                    color = Theme.color.secondary.shadeSecondary
                )

                Text(
                    modifier = Modifier
                        .padding(top = 24.dp, bottom = 8.dp, end = 8.dp)
                        .align(Alignment.End)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            onDismiss()
                        },
                    text = "فهمت ذلك",
                    textAlign = TextAlign.End,
                    style = Theme.textStyle.label.medium,
                    color = Theme.color.primary.primary
                )
            }
        }
    }
}
