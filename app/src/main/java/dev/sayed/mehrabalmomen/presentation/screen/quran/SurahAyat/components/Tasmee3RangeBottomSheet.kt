package dev.sayed.mehrabalmomen.presentation.screen.quran.SurahAyat.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.component.AppTextField
import dev.sayed.mehrabalmomen.design_system.component.BottomSheetDs
import dev.sayed.mehrabalmomen.design_system.component.PrimaryButton
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.base.LocalAppLocale
import dev.sayed.mehrabalmomen.presentation.base.localizedString
import dev.sayed.mehrabalmomen.presentation.base.toLocalizedDigits
import dev.sayed.mehrabalmomen.presentation.components.CheckboxItem

private enum class Tasmee3Mode {
    FULL_SURAH,
    CUSTOM_RANGE
}

/**
 * Bottom Sheet for selecting recitation range (Full Surah vs Custom Verse Range).
 * Built in strict alignment with the app's Design System (matching SettingsBottomSheet, Theme, and Language sheets).
 */
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
    var endAyahText by remember { mutableStateOf(totalAyat.toString()) }

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
            localizedString(R.string.tasmee3_error_start_range, totalAyat.toString().toLocalizedDigits(appLocale))
        endNum == null || endNum < 1 || endNum > totalAyat ->
            localizedString(R.string.tasmee3_error_end_range, totalAyat.toString().toLocalizedDigits(appLocale))
        startNum > endNum ->
            localizedString(R.string.tasmee3_error_start_greater)
        else -> null
    }

    BottomSheetDs(
        onDismiss = onDismiss,
        modifier = modifier
    ) {
        // Header matching SettingsBottomSheet
        Text(
            text = localizedString(R.string.tasmee3_range_title),
            style = Theme.textStyle.label.medium,
            color = Theme.color.primary.shadePrimary
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = localizedString(R.string.tasmee3_range_surah_info, surahName, totalAyat.toString().toLocalizedDigits(appLocale)),
            style = Theme.textStyle.label.medium,
            color = Theme.color.semantic.shadeTertiary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Selection Items using Design System CheckboxItem
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Option 1: Full Surah
            CheckboxItem(
                text = localizedString(R.string.tasmee3_mode_full_surah),
                description = localizedString(R.string.tasmee3_mode_full_surah_desc, totalAyat.toString().toLocalizedDigits(appLocale)),
                isChecked = selectedMode == Tasmee3Mode.FULL_SURAH,
                onCheckedChange = { checked ->
                    if (checked) selectedMode = Tasmee3Mode.FULL_SURAH
                },
                backgroundColor = Theme.color.surfaces.surfaceLow
            )

            // Option 2: Custom Range
            CheckboxItem(
                text = localizedString(R.string.tasmee3_mode_custom_range),
                description = localizedString(R.string.tasmee3_mode_custom_range_desc),
                isChecked = selectedMode == Tasmee3Mode.CUSTOM_RANGE,
                onCheckedChange = { checked ->
                    if (checked) {
                        selectedMode = Tasmee3Mode.CUSTOM_RANGE
                        if (startAyahText.isBlank()) startAyahText = "1"
                        if (endAyahText.isBlank()) endAyahText = totalAyat.toString()
                    }
                },
                backgroundColor = Theme.color.surfaces.surfaceLow
            )

            // Custom Range Inputs using Design System AppTextField
            AnimatedVisibility(
                visible = selectedMode == Tasmee3Mode.CUSTOM_RANGE,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        AppTextField(
                            value = startAyahText,
                            hint = "1",
                            onValueChanged = { input ->
                                startAyahText = input.filter { it.isDigit() }
                            },
                            title = localizedString(R.string.tasmee3_from_ayah),
                            containerColor = Theme.color.surfaces.surfaceLow,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(12.dp),
                            isError = startNum != null && (startNum < 1 || startNum > totalAyat),
                            modifier = Modifier.weight(1f)
                        )

                        AppTextField(
                            value = endAyahText,
                            hint = totalAyat.toString(),
                            onValueChanged = { input ->
                                endAyahText = input.filter { it.isDigit() }
                            },
                            title = localizedString(R.string.tasmee3_to_ayah),
                            containerColor = Theme.color.surfaces.surfaceLow,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(12.dp),
                            isError = endNum != null && (endNum < 1 || endNum > totalAyat || (startNum != null && startNum > endNum)),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    if (errorMessage != null) {
                        Text(
                            text = errorMessage,
                            style = Theme.textStyle.label.small,
                            color = Theme.color.semantic.error,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Button using Design System PrimaryButton
            PrimaryButton(
                modifier = Modifier.fillMaxWidth(),
                text = localizedString(R.string.tasmee3_start_button),
                isEnabled = isRangeValid,
                onClick = {
                    if (selectedMode == Tasmee3Mode.FULL_SURAH) {
                        onConfirm(1, -1)
                    } else if (isRangeValid && startNum != null && endNum != null) {
                        onConfirm(startNum, endNum)
                    }
                }
            )
        }
    }
}
