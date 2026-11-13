package dev.sayed.mehrabalmomen.presentation.screen.quran.tasmee3.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.designsystem.resources.Res
import dev.sayed.mehrabalmomen.designsystem.resources.*
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.base.LocalAppLocale
import dev.sayed.mehrabalmomen.presentation.base.localizedString
import dev.sayed.mehrabalmomen.presentation.base.toLocalizedDigits
import dev.sayed.mehrabalmomen.presentation.components.LinearProgress
import dev.sayed.mehrabalmomen.presentation.components.PrimaryDialog
import org.jetbrains.compose.resources.painterResource

/**
 * Design System compliant Dialog for AI Recitation Model Remote Download & Processing.
 * Strictly aligned with Bookmark RemoveDialog and other core app dialogs.
 */
@Composable
fun RecitationDownloadDialog(
    modifier: Modifier = Modifier,
    isDownloading: Boolean,
    isProcessing: Boolean,
    progress: Float,
    statusText: String,
    errorText: String?,
    onConfirmDownload: () -> Unit,
    onCancel: () -> Unit,
    onContinueInBackground: () -> Unit = onCancel,
    onRetry: () -> Unit
) {
    val appLocale = LocalAppLocale.current
    val isActiveOperation = isDownloading || isProcessing

    PrimaryDialog(
        onDismiss = {
            if (isActiveOperation) onContinueInBackground() else onCancel()
        },
        backgroundColor = Theme.color.surfaces.surface
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Top Close Button matching Design System
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Theme.color.surfaces.surfaceHigh)
                    .clickable {
                        if (isActiveOperation) onContinueInBackground() else onCancel()
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_close),
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
                // Title
                val title = when {
                    errorText != null -> localizedString(Res.string.recitation_download_error_title)
                    isProcessing -> localizedString(Res.string.recitation_download_processing_title)
                    isDownloading -> localizedString(Res.string.recitation_download_in_progress_title)
                    else -> localizedString(Res.string.recitation_download_prompt_title)
                }

                Text(
                    text = title,
                    textAlign = TextAlign.Center,
                    style = Theme.textStyle.title.medium,
                    color = Theme.color.primary.shadePrimary,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Body content based on state
                when {
                    errorText != null -> {
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp),
                            text = localizedString(Res.string.recitation_download_error_prefix, errorText),
                            textAlign = TextAlign.Center,
                            style = Theme.textStyle.body.small,
                            color = Theme.color.semantic.error
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 4.dp, end = 4.dp),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                modifier = Modifier
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) { onCancel() }
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                text = localizedString(Res.string.recitation_download_cancel),
                                style = Theme.textStyle.label.medium,
                                color = Theme.color.secondary.shadeSecondary
                            )

                            Spacer(modifier = Modifier.width(16.dp))

                            Text(
                                modifier = Modifier
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) { onRetry() }
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                text = localizedString(Res.string.recitation_download_retry),
                                style = Theme.textStyle.label.medium,
                                color = Theme.color.primary.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    isProcessing -> {
                        Spacer(modifier = Modifier.height(8.dp))
                        CircularProgressIndicator(
                            color = Theme.color.primary.primary,
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp),
                            text = statusText.ifBlank {
                                localizedString(Res.string.recitation_download_processing_msg)
                            },
                            textAlign = TextAlign.Center,
                            style = Theme.textStyle.body.small,
                            color = Theme.color.secondary.shadeSecondary
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 4.dp, end = 4.dp),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                modifier = Modifier
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) { onCancel() }
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                text = localizedString(Res.string.recitation_download_cancel_action),
                                style = Theme.textStyle.label.medium,
                                color = Theme.color.semantic.error
                            )

                            Spacer(modifier = Modifier.width(16.dp))

                            Text(
                                modifier = Modifier
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) { onContinueInBackground() }
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                text = localizedString(Res.string.recitation_download_continue_background),
                                style = Theme.textStyle.label.medium,
                                color = Theme.color.primary.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    isDownloading -> {
                        Spacer(modifier = Modifier.height(10.dp))
                        LinearProgress(
                            progress = progress,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp),
                            height = 6.dp,
                            colorCompleted = Theme.color.primary.primary,
                            colorRemaining = Theme.color.surfaces.surfaceHigh
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp),
                            text = localizedString(
                                Res.string.recitation_download_progress_msg,
                                (progress * 100).toInt().toString().toLocalizedDigits(appLocale)
                            ),
                            textAlign = TextAlign.Center,
                            style = Theme.textStyle.body.small,
                            color = Theme.color.secondary.shadeSecondary
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 4.dp, end = 4.dp),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                modifier = Modifier
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) { onCancel() }
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                text = localizedString(Res.string.recitation_download_cancel_action),
                                style = Theme.textStyle.label.medium,
                                color = Theme.color.semantic.error
                            )

                            Spacer(modifier = Modifier.width(16.dp))

                            Text(
                                modifier = Modifier
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) { onContinueInBackground() }
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                text = localizedString(Res.string.recitation_download_continue_background),
                                style = Theme.textStyle.label.medium,
                                color = Theme.color.primary.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    else -> {
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp),
                            text = localizedString(Res.string.recitation_download_intro_msg),
                            textAlign = TextAlign.Center,
                            style = Theme.textStyle.body.small,
                            color = Theme.color.secondary.shadeSecondary
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 4.dp, end = 4.dp),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                modifier = Modifier
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) { onCancel() }
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                text = localizedString(Res.string.recitation_download_cancel),
                                style = Theme.textStyle.label.medium,
                                color = Theme.color.secondary.shadeSecondary
                            )

                            Spacer(modifier = Modifier.width(16.dp))

                            Text(
                                modifier = Modifier
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) { onConfirmDownload() }
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                text = localizedString(Res.string.recitation_download_confirm),
                                style = Theme.textStyle.label.medium,
                                color = Theme.color.primary.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
