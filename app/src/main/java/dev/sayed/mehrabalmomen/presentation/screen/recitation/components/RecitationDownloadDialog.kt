package dev.sayed.mehrabalmomen.presentation.screen.recitation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.base.LocalAppLocale
import dev.sayed.mehrabalmomen.presentation.base.localizedString
import dev.sayed.mehrabalmomen.presentation.base.toLocalizedDigits
import dev.sayed.mehrabalmomen.presentation.components.PrimaryDialog

/**
 * Design System Compliant Dialog for AI Recitation Model Remote Download & Processing.
 */
@Composable
fun RecitationDownloadDialog(
    isDownloading: Boolean,
    isProcessing: Boolean,
    progress: Float,
    statusText: String,
    errorText: String?,
    onConfirmDownload: () -> Unit,
    onCancel: () -> Unit,
    onRetry: () -> Unit
) {
    val appLocale = LocalAppLocale.current

    PrimaryDialog(
        onDismiss = onCancel,
        backgroundColor = Theme.color.surfaces.surface
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Top Close Button matching Design System
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Theme.color.surfaces.surface)
                    .clickable { onCancel() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_close),
                    contentDescription = null,
                    tint = Theme.color.primary.primary
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, start = 12.dp, end = 12.dp, bottom = 8.dp)
                    .align(Alignment.TopCenter),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Icon Badge
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Theme.color.surfaces.surfaceHigh),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = if (isProcessing) R.drawable.ic_rotate else R.drawable.ic_download_01),
                        contentDescription = null,
                        tint = Theme.color.primary.primary,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = when {
                        errorText != null -> localizedString(R.string.recitation_download_error_title)
                        isProcessing -> localizedString(R.string.recitation_download_processing_title)
                        isDownloading -> localizedString(R.string.recitation_download_in_progress_title)
                        else -> localizedString(R.string.recitation_download_prompt_title)
                    },
                    textAlign = TextAlign.Center,
                    style = Theme.textStyle.title.medium,
                    color = Theme.color.primary.shadePrimary
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (errorText != null) {
                    Text(
                        text = localizedString(R.string.recitation_download_error_prefix, errorText),
                        textAlign = TextAlign.Center,
                        style = Theme.textStyle.body.small,
                        color = Color.Red
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Theme.color.primary.primary)
                            .clickable { onRetry() }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = localizedString(R.string.recitation_download_retry),
                            color = Theme.color.primary.onPrimary,
                            style = Theme.textStyle.label.medium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        modifier = Modifier
                            .clickable { onCancel() }
                            .padding(vertical = 8.dp),
                        text = localizedString(R.string.recitation_download_cancel),
                        textAlign = TextAlign.Center,
                        style = Theme.textStyle.label.medium,
                        color = Theme.color.secondary.shadeSecondary
                    )
                } else if (isProcessing) {
                    Spacer(modifier = Modifier.height(8.dp))
                    CircularProgressIndicator(
                        color = Theme.color.primary.primary,
                        strokeWidth = 3.dp,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = localizedString(R.string.recitation_download_processing_msg),
                        textAlign = TextAlign.Center,
                        style = Theme.textStyle.body.small,
                        color = Theme.color.secondary.shadeSecondary
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        modifier = Modifier
                            .clickable { onCancel() }
                            .padding(vertical = 8.dp),
                        text = localizedString(R.string.recitation_download_cancel),
                        textAlign = TextAlign.Center,
                        style = Theme.textStyle.label.medium,
                        color = Theme.color.secondary.shadeSecondary
                    )
                } else if (isDownloading) {
                    Spacer(modifier = Modifier.height(8.dp))
                    dev.sayed.mehrabalmomen.presentation.components.LinearProgress(
                        progress = progress,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = localizedString(
                            R.string.recitation_download_progress_msg,
                            (progress * 100).toInt().toString().toLocalizedDigits(appLocale)
                        ),
                        textAlign = TextAlign.Center,
                        style = Theme.textStyle.body.small,
                        color = Theme.color.secondary.shadeSecondary
                    )
                    Spacer(modifier = Modifier.height(20.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Theme.color.surfaces.surfaceHigh)
                            .clickable { onCancel() }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = localizedString(R.string.recitation_download_cancel_long),
                            color = Theme.color.primary.primary,
                            style = Theme.textStyle.label.medium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    Text(
                        text = localizedString(R.string.recitation_download_intro_msg),
                        textAlign = TextAlign.Center,
                        style = Theme.textStyle.body.small,
                        color = Theme.color.secondary.shadeSecondary
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Theme.color.primary.primary)
                            .clickable { onConfirmDownload() }
                            .padding(vertical = 14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = localizedString(R.string.recitation_download_confirm),
                            color = Theme.color.primary.onPrimary,
                            style = Theme.textStyle.label.medium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        modifier = Modifier
                            .clickable { onCancel() }
                            .padding(vertical = 8.dp),
                        text = localizedString(R.string.recitation_download_cancel),
                        textAlign = TextAlign.Center,
                        style = Theme.textStyle.label.medium,
                        color = Theme.color.secondary.shadeSecondary
                    )
                }
            }
        }
    }
}
