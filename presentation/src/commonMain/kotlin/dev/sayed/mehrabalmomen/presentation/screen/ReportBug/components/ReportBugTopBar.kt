package dev.sayed.mehrabalmomen.presentation.screen.ReportBug.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.sayed.mehrabalmomen.design_system.component.AppBar
import dev.sayed.mehrabalmomen.designsystem.resources.Res
import dev.sayed.mehrabalmomen.designsystem.resources.*
import dev.sayed.mehrabalmomen.presentation.base.localizedString

@Composable
fun ReportBugTopBar(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AppBar(
        modifier = modifier,
        title = localizedString(Res.string.help_feedback),
        onBackClick = onBackClick
    )
}
