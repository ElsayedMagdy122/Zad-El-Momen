@file:OptIn(kotlin.time.ExperimentalTime::class)

package dev.sayed.mehrabalmomen.presentation.screen.notifications.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.domain.model.notification.AppNotification
import dev.sayed.mehrabalmomen.domain.model.notification.NotificationType
import kotlin.time.Clock
import org.jetbrains.compose.resources.painterResource
import zadelmomen.presentation.generated.resources.Res
import zadelmomen.presentation.generated.resources.ic_download_01
import zadelmomen.presentation.generated.resources.ic_notifications
import zadelmomen.presentation.generated.resources.ic_quran
import zadelmomen.presentation.generated.resources.ic_radio_selected

@Composable
fun NotificationItem(
    notification: AppNotification,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Solid opaque background to prevent delete background from bleeding through
    val backgroundColor = if (!notification.isRead) {
        Theme.color.surfaces.surfaceHigh
    } else {
        Theme.color.surfaces.surfaceLow
    }

    val typeIconColor = when (notification.type) {
        NotificationType.NEW_RECITER -> Color(0xFF10B981) // Green
        NotificationType.NEW_RADIO -> Color(0xFF3B82F6)   // Blue
        NotificationType.APP_UPDATE -> Color(0xFFF59E0B)  // Orange
        NotificationType.GENERAL -> Theme.color.primary.primary
    }

    val iconResource = when (notification.type) {
        NotificationType.NEW_RECITER -> Res.drawable.ic_quran
        NotificationType.NEW_RADIO -> Res.drawable.ic_radio_selected
        NotificationType.APP_UPDATE -> Res.drawable.ic_download_01
        NotificationType.GENERAL -> Res.drawable.ic_notifications
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon container
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(typeIconColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(iconResource),
                contentDescription = null,
                tint = typeIconColor,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Content
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = notification.title,
                    style = Theme.textStyle.label.medium,
                    fontWeight = if (!notification.isRead) FontWeight.Bold else FontWeight.SemiBold,
                    color = Theme.color.primary.shadePrimary,
                    modifier = Modifier.weight(1f)
                )

                if (!notification.isRead) {
                    Box(
                        modifier = Modifier
                            .padding(start = 6.dp)
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Theme.color.primary.primary)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = notification.body,
                style = Theme.textStyle.body.small,
                color = Theme.color.secondary.shadeSecondary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = formatTimeAgo(notification.createdAt),
                style = Theme.textStyle.label.small,
                color = Theme.color.secondary.shadeSecondary.copy(alpha = 0.6f)
            )
        }
    }
}

private fun formatTimeAgo(timestamp: Long): String {
    if (timestamp <= 0L) return ""
    val now = Clock.System.now().toEpochMilliseconds()
    val diffInSeconds = (now - timestamp) / 1000L
    return when {
        diffInSeconds < 60L -> "الآن"
        diffInSeconds < 3600L -> "منذ ${diffInSeconds / 60L} دقيقة"
        diffInSeconds < 86400L -> "منذ ${diffInSeconds / 3600L} ساعة"
        else -> "منذ ${diffInSeconds / 86400L} يوم"
    }
}
