package dev.sayed.mehrabalmomen.presentation.screen.reminders.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.component.AppTextField
import dev.sayed.mehrabalmomen.design_system.component.BottomSheetDs
import dev.sayed.mehrabalmomen.design_system.component.Chip
import dev.sayed.mehrabalmomen.design_system.component.PrimaryButton
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.domain.model.RecurrencePattern
import dev.sayed.mehrabalmomen.presentation.base.LocalAppLocale
import dev.sayed.mehrabalmomen.presentation.base.localizeAmPm
import dev.sayed.mehrabalmomen.presentation.base.localizedString
import dev.sayed.mehrabalmomen.presentation.base.toLocalizedDigits
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddReminderBottomSheet(
    title: String,
    onTitleChange: (String) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit,
    hour: Int,
    minute: Int,
    onTimeClick: () -> Unit,
    selectedRecurrence: RecurrencePattern,
    onRecurrenceSelect: (RecurrencePattern) -> Unit,
    onSaveClick: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    isEditMode: Boolean = false,
    isPrePrayerMode: Boolean = false,
    onMinuteChange: ((Int) -> Unit)? = null
) {
    val language = LocalAppLocale.current

    val hour12 = when {
        hour == 0 -> 12
        hour > 12 -> hour - 12
        else -> hour
    }
    val amPm = if (hour >= 12) "PM" else "AM"
    val formattedTime = String.format(
        Locale.ENGLISH,
        "%02d:%02d %s",
        hour12,
        minute,
        amPm
    ).toLocalizedDigits(language).localizeAmPm(language)

    BottomSheetDs(
        onDismiss = onDismiss,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = when {
                    isPrePrayerMode -> "تعديل التنبيه قبل الصلاة"
                    isEditMode -> "تعديل التنبيه"
                    else -> "إضافة تنبيه جديد"
                },
                style = Theme.textStyle.title.medium,
                color = Theme.color.primary.primary,
                fontWeight = FontWeight.Bold
            )

            if (!isPrePrayerMode) {
                // Title input matching design: "اسم التذكير"
                AppTextField(
                    value = title,
                    hint = "أذكار الصباح",
                    onValueChanged = onTitleChange,
                    title = "اسم التذكير",
                    containerColor = Theme.color.surfaces.surfaceLow,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Description input matching design: "نص التنبيه"
            AppTextField(
                value = description,
                hint = if (isPrePrayerMode) "اقترب موعد الأذان، استعد للوضوء والصلاة" else "لا تنسَ أذكار الصباح والتحصين",
                onValueChanged = onDescriptionChange,
                title = "نص التنبيه",
                containerColor = Theme.color.surfaces.surfaceLow,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            )

            if (isPrePrayerMode) {
                // Duration selection for pre-prayer: 5, 10, 15, 20 minutes
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "التنبيه قبل الأذان بـ",
                        style = Theme.textStyle.label.medium,
                        color = Theme.color.primary.shadePrimary
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val durationOptions = listOf(
                            5 to "٥ دقائق",
                            10 to "١٠ دقائق",
                            15 to "١٥ دقيقة",
                            20 to "٢٠ دقيقة"
                        )

                        durationOptions.forEach { (mins, label) ->
                            Chip(
                                text = label,
                                isSelected = minute == mins,
                                onClick = { onMinuteChange?.invoke(mins) },
                                shape = RoundedCornerShape(100.dp),
                                textStyle = Theme.textStyle.label.medium
                            )
                        }
                    }
                }
            } else {
                // Time Selector Card
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Theme.color.surfaces.surfaceLow)
                        .clickable { onTimeClick() }
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Theme.color.primary.primary.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_reminder),
                                contentDescription = null,
                                tint = Theme.color.primary.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Text(
                            text = "وقت التنبيه",
                            style = Theme.textStyle.label.medium,
                            color = Theme.color.primary.shadePrimary
                        )
                    }

                    Text(
                        text = formattedTime,
                        style = Theme.textStyle.title.medium,
                        color = Theme.color.primary.primary,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Recurrence Chips
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "التكرار",
                        style = Theme.textStyle.label.medium,
                        color = Theme.color.primary.shadePrimary
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val recurrenceOptions = listOf(
                            RecurrencePattern.DAILY to "يومي",
                            RecurrencePattern.WEEKLY to "أسبوعي",
                            RecurrencePattern.ONCE to "مرة واحدة"
                        )

                        recurrenceOptions.forEach { (pattern, label) ->
                            Chip(
                                text = label,
                                isSelected = selectedRecurrence == pattern,
                                onClick = { onRecurrenceSelect(pattern) },
                                shape = RoundedCornerShape(100.dp),
                                textStyle = Theme.textStyle.label.medium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            PrimaryButton(
                text = localizedString(R.string.save),
                onClick = onSaveClick,
                isEnabled = if (isPrePrayerMode) description.isNotBlank() else title.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
