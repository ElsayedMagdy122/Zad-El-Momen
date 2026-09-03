package dev.sayed.mehrabalmomen.presentation.screen.ReportBug.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.component.AppBar
import dev.sayed.mehrabalmomen.presentation.base.localizedString

@Composable
fun ReportBugTopBar(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier) {
    AppBar(
        modifier = modifier,
        title = localizedString(R.string.help_feedback),
        onBackClick =onBackClick
    )
}