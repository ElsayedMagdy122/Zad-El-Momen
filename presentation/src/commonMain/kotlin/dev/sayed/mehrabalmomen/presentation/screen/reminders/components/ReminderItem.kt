package dev.sayed.mehrabalmomen.presentation.screen.reminders.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.design_system.component.Switch
import dev.sayed.mehrabalmomen.design_system.theme.MehrabTheme
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.designsystem.resources.Res
import dev.sayed.mehrabalmomen.designsystem.resources.ic_calendar_small
import dev.sayed.mehrabalmomen.designsystem.resources.ic_clock_small
import dev.sayed.mehrabalmomen.designsystem.resources.ic_close
import dev.sayed.mehrabalmomen.designsystem.resources.ic_pencil_edit
import dev.sayed.mehrabalmomen.designsystem.resources.remove
import dev.sayed.mehrabalmomen.domain.model.AppSettings
import dev.sayed.mehrabalmomen.domain.model.RecurrencePattern
import dev.sayed.mehrabalmomen.domain.model.ReminderConfig
import dev.sayed.mehrabalmomen.domain.model.ReminderType
import dev.sayed.mehrabalmomen.presentation.base.LocalAppLocale
import dev.sayed.mehrabalmomen.presentation.base.localizedString
import dev.sayed.mehrabalmomen.presentation.components.PrimaryDialog
import dev.sayed.mehrabalmomen.presentation.screen.reminders.getRecurrenceText
import dev.sayed.mehrabalmomen.presentation.screen.reminders.getReminderIcon
import dev.sayed.mehrabalmomen.presentation.screen.reminders.getReminderTitle
import dev.sayed.mehrabalmomen.presentation.utils.localizeAmPm
import dev.sayed.mehrabalmomen.presentation.utils.toLocalizedDigits
import androidx.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.resources.painterResource

@Composable
fun ReminderItem(
    config: ReminderConfig,
    onToggle: (Boolean) -> Unit,
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier,
    onTimeClick: (() -> Unit)? = null,
    onRecurrenceClick: (() -> Unit)? = null
) {
    val language = LocalAppLocale.current

    val hour12 = when {
        config.hour == 0 -> 12
        config.hour > 12 -> config.hour - 12
        else -> config.hour
    }

    val amPm = if (config.hour >= 12) "PM" else "AM"
    val h = hour12.toString().padStart(2, '0')
    val m = config.minute.toString().padStart(2, '0')
    val formattedTime = "$h:$m $amPm".toLocalizedDigits(language).localizeAmPm(language)

    val icon = getReminderIcon(config)
    val title = getReminderTitle(config)
    val recurrenceLabel = if (config.type == ReminderType.BEFORE_PRAYER) {
        "كل صلاة"
    } else {
        getRecurrenceText(config.recurrence)
    }

    val timeBadgeText = if (config.type == ReminderType.BEFORE_PRAYER) {
        val duration = config.minute.coerceAtLeast(1)
        "قبل الأذان بـ $duration دقيقة".toLocalizedDigits(language)
    } else {
        formattedTime
    }

    val descriptionText = config.description.ifBlank {
        when (config.type) {
            ReminderType.MORNING_AZKAR -> "لا تنس أذكار الصباح والتحصين"
            ReminderType.EVENING_AZKAR -> "لا تنس أذكار المساء لحفظك ونور صدرك"
            ReminderType.DAILY_WORD -> "حان وقت وردك القرآني اليومي"
            ReminderType.FRIDAY_SUNNAN -> "سنن الجمعة وقراءة سورة الكهف والصلاة على النبي"
            ReminderType.BEFORE_PRAYER -> "اقترب موعد الأذان، استعد للوضوء والصلاة"
            null -> ""
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Theme.color.surfaces.surfaceLow)
            .padding(16.dp)
    ) {
        // Top Row: Icon + Title on Start, Switch on End
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f, fill = false)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Theme.color.surfaces.surfaceHigh),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        painter = painterResource(icon.resource),
                        contentDescription = null,
                        tint = Theme.color.primary.primary
                    )
                }

                Text(
                    text = title.asString(),
                    color = Theme.color.primary.shadePrimary,
                    style = Theme.textStyle.title.small,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Switch(
                isChecked = config.isEnabled,
                onCheckedChange = onToggle
            )
        }

        // Description
        if (descriptionText.isNotBlank()) {
            Text(
                text = descriptionText,
                color = Theme.color.secondary.shadeSecondary,
                style = Theme.textStyle.body.small,
                modifier = Modifier.padding(top = 8.dp),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Meta info row: Time & Recurrence
        Row(
            modifier = Modifier.padding(top = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Time Badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    (onTimeClick ?: onEditClick)()
                }
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_clock_small),
                    contentDescription = null,
                    tint = Theme.color.secondary.shadeSecondary,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = timeBadgeText,
                    color = Theme.color.secondary.shadeSecondary,
                    style = Theme.textStyle.label.small
                )
            }

            // Recurrence Badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    (onRecurrenceClick ?: onEditClick)()
                }
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_calendar_small),
                    contentDescription = null,
                    tint = Theme.color.secondary.shadeSecondary,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = recurrenceLabel,
                    color = Theme.color.secondary.shadeSecondary,
                    style = Theme.textStyle.label.small
                )
            }
        }

        // Horizontal Divider
        Box(
            modifier = Modifier
                .padding(top = 14.dp, bottom = 12.dp)
                .fillMaxWidth()
                .height(1.dp)
                .background(Theme.color.surfaces.surfaceHigh)
        )

        // Bottom Action Row: Status on Start, Edit Button on End
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = if (config.isEnabled) "مفعّل" else "معطّل",
                color = if (config.isEnabled) Theme.color.secondary.shadeSecondary else Theme.color.semantic.disabled,
                style = Theme.textStyle.label.small
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    onEditClick()
                }
            ) {
                Text(
                    text = "تعديل التذكير",
                    color = Theme.color.primary.shadePrimary,
                    style = Theme.textStyle.label.small
                )
                Icon(
                    painter = painterResource(Res.drawable.ic_pencil_edit),
                    contentDescription = "تعديل التذكير",
                    tint = Theme.color.primary.shadePrimary,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

@Composable
fun RemoveReminderDialog(
    reminderTitle: String = "",
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier
) {
    PrimaryDialog(onDismiss = onDismiss) {
        var isVisible by remember { mutableStateOf(false) }
        LaunchedEffect(Unit) {
            isVisible = true
        }

        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(tween(250)) + scaleIn(
                initialScale = 0.92f,
                animationSpec = tween(250, easing = FastOutSlowInEasing)
            ),
            exit = fadeOut(tween(150)) + scaleOut(
                targetScale = 0.92f,
                animationSpec = tween(150)
            )
        ) {
            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Theme.color.surfaces.surface)
                        .clickable { onDismiss() }
                    ,
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_close),
                        contentDescription = "إغلاق",
                        tint = Theme.color.primary.primary,
                        modifier = Modifier.size(14.dp)
                    )
                }

                Column(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "حذف التنبيه",
                        textAlign = TextAlign.Center,
                        style = Theme.textStyle.title.medium,
                        color = Theme.color.primary.shadePrimary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (reminderTitle.isNotBlank()) "هل أنت متأكد من رغبتك في حذف تنبيه \"$reminderTitle\"؟"
                        else "هل أنت متأكد من رغبتك في حذف هذا التنبيه؟",
                        textAlign = TextAlign.Center,
                        style = Theme.textStyle.body.small,
                        color = Theme.color.secondary.shadeSecondary
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "إلغاء",
                            style = Theme.textStyle.label.medium,
                            color = Theme.color.secondary.shadeSecondary,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onDismiss() }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = localizedString(Res.string.remove),
                            style = Theme.textStyle.label.medium,
                            color = Theme.color.semantic.error,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onConfirm() }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun ReminderItemPreview() {
    MehrabTheme(
        language = AppSettings.Language.ARABIC,
        isDarkTheme = false
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ReminderItem(
                config = ReminderConfig(
                    id = 0L,
                    title = "التنبيه قبل الصلاة",
                    description = "اقترب موعد الأذان، استعد للوضوء والصلاة",
                    hour = 0,
                    minute = 15,
                    recurrence = RecurrencePattern.DAILY,
                    isEnabled = true,
                    type = ReminderType.BEFORE_PRAYER
                ),
                onToggle = {},
                onEditClick = {}
            )
        }
    }
}
