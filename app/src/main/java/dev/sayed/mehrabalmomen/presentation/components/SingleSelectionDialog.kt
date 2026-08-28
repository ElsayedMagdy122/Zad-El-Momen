package dev.sayed.mehrabalmomen.presentation.components

import android.os.Build
import android.view.WindowManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogWindowProvider
import dev.sayed.mehrabalmomen.design_system.theme.MehrabTheme
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.base.LocalAppLocale
import dev.sayed.mehrabalmomen.presentation.base.LocalIsDarkTheme

@Composable
fun PrimaryDialog(
    onDismiss: () -> Unit,
    backgroundColor: Color = Theme.color.surfaces.surfaceHigh,
    content: @Composable ColumnScope.() -> Unit
) {
    val language = LocalAppLocale.current
    val isDark = LocalIsDarkTheme.current
    
    Dialog(
        onDismissRequest = onDismiss
    ) {
        MehrabTheme(
            language = language,
            isDarkTheme = isDark
        ) {
            val dialogWindow = (LocalView.current.parent as? DialogWindowProvider)?.window
            LaunchedEffect(dialogWindow) {
                dialogWindow?.let { window ->
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        window.setBackgroundBlurRadius(20)
                    }
                    window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
                    window.setDimAmount(0.35f)
                }
            }

            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(backgroundColor)
                    .widthIn(max = 400.dp)
            ) {
                content()
            }
        }
    }
}
