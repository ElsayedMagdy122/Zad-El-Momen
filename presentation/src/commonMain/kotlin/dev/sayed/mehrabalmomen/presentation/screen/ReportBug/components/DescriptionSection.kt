package dev.sayed.mehrabalmomen.presentation.screen.ReportBug.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.design_system.component.AppBasicTextField
import dev.sayed.mehrabalmomen.presentation.base.localizedString
import zadelmomen.presentation.generated.resources.Res
import zadelmomen.presentation.generated.resources.description
import zadelmomen.presentation.generated.resources.please_describe_the_issue_and_steps_to_reproduce_it

@Composable
fun DescriptionSection(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        AppBasicTextField(
            value = value,
            title = localizedString(Res.string.description),
            hint = localizedString(Res.string.please_describe_the_issue_and_steps_to_reproduce_it),
            onValueChanged = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp),
            singleLine = false,
            minLines = 5,
            maxLines = 10
        )
    }
}
