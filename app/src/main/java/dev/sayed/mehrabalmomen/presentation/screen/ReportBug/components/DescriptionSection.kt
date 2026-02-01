package dev.sayed.mehrabalmomen.presentation.screen.ReportBug.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.base.localizedString

@Composable
fun DescriptionSection(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Theme.color.surfaces.surfaceLow)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SectionTitle(localizedString(R.string.description))

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    localizedString(R.string.please_describe_the_issue_and_steps_to_reproduce_it),
                    style = Theme.textStyle.body.small,
                    color = Theme.color.secondary.shadeSecondary
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(

                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                disabledBorderColor = Color.Transparent,
                errorBorderColor = Color.Transparent,


                focusedContainerColor = Theme.color.surfaces.surfaceHigh,
                unfocusedContainerColor = Theme.color.surfaces.surfaceHigh,
                disabledContainerColor = Theme.color.surfaces.surfaceHigh,


                cursorColor = Theme.color.secondary.shadeSecondary,
                focusedTextColor = Theme.color.secondary.shadeSecondary,
                unfocusedTextColor = Theme.color.secondary.shadeSecondary
            ),
            maxLines = 10,
            minLines = 5
        )
    }
}