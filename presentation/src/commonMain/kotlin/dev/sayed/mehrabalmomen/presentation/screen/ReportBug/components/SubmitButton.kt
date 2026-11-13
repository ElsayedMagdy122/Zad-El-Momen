package dev.sayed.mehrabalmomen.presentation.screen.ReportBug.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.sayed.mehrabalmomen.design_system.component.PrimaryButton
import dev.sayed.mehrabalmomen.designsystem.resources.Res
import dev.sayed.mehrabalmomen.designsystem.resources.*
import dev.sayed.mehrabalmomen.presentation.base.localizedString

@Composable
fun SubmitButton(
    loading: Boolean,
    onClick: () -> Unit
) {
    PrimaryButton(
        text = if (loading) localizedString(Res.string.sending) else localizedString(Res.string.submit_report),
        isEnabled = !loading,
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    )
}
