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
import dev.sayed.mehrabalmomen.design_system.component.AppBasicTextField
import dev.sayed.mehrabalmomen.design_system.component.BottomSheetDs
import dev.sayed.mehrabalmomen.design_system.component.PrimaryButton
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.base.LocalAppLocale
import dev.sayed.mehrabalmomen.presentation.base.localizedString
import dev.sayed.mehrabalmomen.presentation.base.toLocalizedDigits
import dev.sayed.mehrabalmomen.presentation.components.CheckboxItem
import zadelmomen.presentation.generated.resources.Res
import zadelmomen.presentation.generated.resources.tasmee3_error_end_range
import zadelmomen.presentation.generated.resources.tasmee3_error_start_greater
import zadelmomen.presentation.generated.resources.tasmee3_error_start_range
import zadelmomen.presentation.generated.resources.tasmee3_from_ayah
import zadelmomen.presentation.generated.resources.tasmee3_mode_custom_range
import zadelmomen.presentation.generated.resources.tasmee3_mode_custom_range_desc
import zadelmomen.presentation.generated.resources.tasmee3_mode_full_surah
import zadelmomen.presentation.generated.resources.tasmee3_mode_full_surah_desc
import zadelmomen.presentation.generated.resources.confirm
import zadelmomen.presentation.generated.resources.tasmee3_range_surah_info
import zadelmomen.presentation.generated.resources.tasmee3_range_title
import zadelmomen.presentation.generated.resources.tasmee3_to_ayah

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
            localizedString(Res.string.tasmee3_error_start_range, totalAyat.toString().toLocalizedDigits(appLocale))
        endNum == null || endNum < 1 || endNum > totalAyat ->
            localizedString(Res.string.tasmee3_error_end_range, totalAyat.toString().toLocalizedDigits(appLocale))
        startNum > endNum ->
            localizedString(Res.string.tasmee3_error_start_greater)
        else -> null
    }

    BottomSheetDs(
        onDismiss = onDismiss,
        modifier = modifier
    ) {
        Text(
            text = localizedString(Res.string.tasmee3_range_title),
            style = Theme.textStyle.label.medium,
            color = Theme.color.primary.shadePrimary
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = localizedString(Res.string.tasmee3_range_surah_info, surahName, totalAyat.toString().toLocalizedDigits(appLocale)),
            style = Theme.textStyle.label.medium,
            color = Theme.color.semantic.shadeTertiary
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CheckboxItem(
                text = localizedString(Res.string.tasmee3_mode_full_surah),
                description = localizedString(Res.string.tasmee3_mode_full_surah_desc, totalAyat.toString().toLocalizedDigits(appLocale)),
                isChecked = selectedMode == Tasmee3Mode.FULL_SURAH,
                onCheckedChange = { checked ->
                    if (checked) selectedMode = Tasmee3Mode.FULL_SURAH
                },
                backgroundColor = Theme.color.surfaces.surfaceLow
            )

            CheckboxItem(
                text = localizedString(Res.string.tasmee3_mode_custom_range),
                description = localizedString(Res.string.tasmee3_mode_custom_range_desc),
                isChecked = selectedMode == Tasmee3Mode.CUSTOM_RANGE,
                onCheckedChange = { checked ->
                    if (checked) selectedMode = Tasmee3Mode.CUSTOM_RANGE
                },
                backgroundColor = Theme.color.surfaces.surfaceLow
            )
        }

        AnimatedVisibility(
            visible = selectedMode == Tasmee3Mode.CUSTOM_RANGE,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
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
                    AppBasicTextField(
                        modifier = Modifier.weight(1f),
                        value = startAyahText,
                        onValueChanged = { input ->
                            startAyahText = input.filter { it.isDigit() }
                        },
                        hint = localizedString(Res.string.tasmee3_from_ayah),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        isError = startNum == null || startNum < 1 || startNum > totalAyat
                    )

                    AppBasicTextField(
                        modifier = Modifier.weight(1f),
                        value = endAyahText,
                        onValueChanged = { input ->
                            endAyahText = input.filter { it.isDigit() }
                        },
                        hint = localizedString(Res.string.tasmee3_to_ayah),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        isError = endNum == null || endNum < 1 || endNum > totalAyat
                    )
                }

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = errorMessage,
                        style = Theme.textStyle.label.small,
                        color = Theme.color.semantic.error
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        PrimaryButton(
            text = localizedString(Res.string.confirm),
            isEnabled = isRangeValid,
            onClick = {
                val finalStart = if (selectedMode == Tasmee3Mode.FULL_SURAH) 1 else (startNum ?: 1)
                val finalEnd = if (selectedMode == Tasmee3Mode.FULL_SURAH) totalAyat else (endNum ?: totalAyat)
                onConfirm(finalStart, finalEnd)
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}
