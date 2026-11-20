package dev.sayed.mehrabalmomen.presentation.screen.ReportBug.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.sayed.mehrabalmomen.design_system.component.AppBasicTextField
import dev.sayed.mehrabalmomen.presentation.base.localizedString
import zadelmomen.presentation.generated.resources.Res
import zadelmomen.presentation.generated.resources.brief_summary_of_the_issue
import zadelmomen.presentation.generated.resources.bug_title

@Composable
fun BugTitleSection(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        AppBasicTextField(
            value = value,
            title = localizedString(Res.string.bug_title),
            hint = localizedString(Res.string.brief_summary_of_the_issue),
            onValueChanged = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
    }
}
