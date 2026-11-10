package dev.sayed.mehrabalmomen.presentation.screen.quran.SurahAyat.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.design_system.component.AppBasicTextField
import dev.sayed.mehrabalmomen.design_system.component.BottomSheetDs
import dev.sayed.mehrabalmomen.design_system.component.PrimaryButton
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.base.LocalAppLocale
import dev.sayed.mehrabalmomen.presentation.base.toLocalizedDigits

private enum class Tasmee3Mode {
    FULL_SURAH,
    CUSTOM_RANGE
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Tasmee3RangeBottomSheet(
    surahName: String,
    totalAyat: Int,
    onDismiss: () -> Unit,
    onConfirm: (startAyah: Int, endAyah: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val appLocale = LocalAppLocale.current

    var selectedMode by remember { mutableStateOf(Tasmee3Mode.FULL_SURAH) }
    var startAyahText by remember { mutableStateOf("1") }
    var endAyahText by remember {
        mutableStateOf(totalAyat.toString())
    }

    val startNum = startAyahText.toIntOrNull()
    val endNum = endAyahText.toIntOrNull()

    val isRangeValid = if (selectedMode == Tasmee3Mode.FULL_SURAH) {
        true
    } else {
        startNum != null && endNum != null &&
                startNum in 1..totalAyat &&
                endNum in 1..totalAyat &&
                startNum <= endNum
    }

    val errorMessage: String? = when {
        selectedMode == Tasmee3Mode.FULL_SURAH -> null
        startNum == null || startNum < 1 || startNum > totalAyat ->
            "بداية الآيات يجب أن تكون بين 1 و ${totalAyat.toString().toLocalizedDigits(appLocale)}"
        endNum == null || endNum < 1 || endNum > totalAyat ->
            "نهاية الآيات يجب أن تكون بين 1 و ${totalAyat.toString().toLocalizedDigits(appLocale)}"
        startNum > endNum ->
            "آية البداية يجب أن تكون أصغر من أو تساوي آية النهاية"
        else -> null
    }

    BottomSheetDs(
        onDismiss = onDismiss,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            // Header
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "تحديد نطاق التسميع",
                    style = Theme.textStyle.title.medium,
                    color = Theme.color.primary.shadePrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "سورة $surahName • عدد الآيات: ${totalAyat.toString().toLocalizedDigits(appLocale)} آية",
                    style = Theme.textStyle.body.small,
                    color = Theme.color.semantic.shadeTertiary
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Option 1: Full Surah
            OptionSelectionCard(
                title = "السورة كاملة",
                subtitle = "من الآية ١ إلى الآية ${totalAyat.toString().toLocalizedDigits(appLocale)}",
                isSelected = selectedMode == Tasmee3Mode.FULL_SURAH,
                onClick = { selectedMode = Tasmee3Mode.FULL_SURAH }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Option 2: Custom Range
            OptionSelectionCard(
                title = "تحديد مقطع من الآيات",
                subtitle = "تسميع جزء محدد من السورة",
                isSelected = selectedMode == Tasmee3Mode.CUSTOM_RANGE,
                onClick = {
                    selectedMode = Tasmee3Mode.CUSTOM_RANGE
                    if (startAyahText.isBlank()) startAyahText = "1"
                    if (endAyahText.isBlank()) endAyahText = totalAyat.toString()
                }
            )

            // Custom Range Inputs
            AnimatedVisibility(
                visible = selectedMode == Tasmee3Mode.CUSTOM_RANGE,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Start Ayah Input
                        AyahNumberField(
                            label = "من الآية",
                            value = startAyahText,
                            onValueChange = { input ->
                                startAyahText = input.filter { it.isDigit() }
                            },
                            onIncrement = {
                                val current = startAyahText.toIntOrNull() ?: 1
                                if (current < totalAyat) {
                                    startAyahText = (current + 1).toString()
                                }
                            },
                            onDecrement = {
                                val current = startAyahText.toIntOrNull() ?: 1
                                if (current > 1) {
                                    startAyahText = (current - 1).toString()
                                }
                            },
                            modifier = Modifier.weight(1f)
                        )

                        // End Ayah Input
                        AyahNumberField(
                            label = "إلى الآية",
                            value = endAyahText,
                            onValueChange = { input ->
                                endAyahText = input.filter { it.isDigit() }
                            },
                            onIncrement = {
                                val current = endAyahText.toIntOrNull() ?: totalAyat
                                if (current < totalAyat) {
                                    endAyahText = (current + 1).toString()
                                }
                            },
                            onDecrement = {
                                val current = endAyahText.toIntOrNull() ?: totalAyat
                                if (current > 1) {
                                    endAyahText = (current - 1).toString()
                                }
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    if (errorMessage != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = errorMessage,
                            style = Theme.textStyle.body.small,
                            color = Theme.color.semantic.error
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Confirm Button
            PrimaryButton(
                text = "بدء التسميع",
                onClick = {
                    if (selectedMode == Tasmee3Mode.FULL_SURAH) {
                        onConfirm(1, -1)
                    } else if (isRangeValid && startNum != null && endNum != null) {
                        onConfirm(startNum, endNum)
                    }
                },
                isEnabled = isRangeValid,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun OptionSelectionCard(
    title: String,
    subtitle: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isSelected) Theme.color.primary.primary else Theme.color.surfaces.surfaceHigh
    val bgColor = if (isSelected) Theme.color.primary.primary.copy(alpha = 0.08f) else Theme.color.surfaces.surfaceHigh

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(bgColor)
            .border(
                width = if (isSelected) 1.5.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Radio / Selection Indicator
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .border(
                    width = 2.dp,
                    color = if (isSelected) Theme.color.primary.primary else Theme.color.semantic.shadeTertiary,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(Theme.color.primary.primary)
                )
            }
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = Theme.textStyle.title.small,
                color = if (isSelected) Theme.color.primary.shadePrimary else Theme.color.secondary.secondaryText
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = Theme.textStyle.body.small,
                color = Theme.color.semantic.shadeTertiary
            )
        }
    }
}

@Composable
private fun AyahNumberField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = Theme.textStyle.label.medium,
            color = Theme.color.secondary.secondaryText
        )
        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Theme.color.surfaces.surfaceHigh)
                .border(1.dp, Theme.color.surfaces.surfaceHigh, RoundedCornerShape(12.dp))
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Decrement Button
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Theme.color.surfaces.surface)
                    .clickable(onClick = onDecrement),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "-",
                    style = Theme.textStyle.title.small,
                    color = Theme.color.secondary.secondaryText
                )
            }

            AppBasicTextField(
                value = value,
                hint = "",
                onValueChanged = onValueChange,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                containerColor = Color.Transparent,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp)
            )

            // Increment Button
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Theme.color.surfaces.surface)
                    .clickable(onClick = onIncrement),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "+",
                    style = Theme.textStyle.title.small,
                    color = Theme.color.secondary.secondaryText
                )
            }
        }
    }
}
