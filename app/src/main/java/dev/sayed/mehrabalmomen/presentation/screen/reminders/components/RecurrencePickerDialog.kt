package dev.sayed.mehrabalmomen.presentation.screen.reminders.components

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.component.PrimaryButton
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.domain.model.RecurrencePattern
import dev.sayed.mehrabalmomen.presentation.base.localizedString
import dev.sayed.mehrabalmomen.presentation.components.PrimaryDialog

@Composable
fun RecurrencePickerDialog(
    initialRecurrence: RecurrencePattern,
    onDismiss: () -> Unit,
    onConfirm: (RecurrencePattern) -> Unit,
    modifier: Modifier = Modifier
) {
    var selected by remember { mutableStateOf(initialRecurrence) }

    val options = listOf(
        RecurrencePattern.DAILY to "يومي (كل يوم)",
        RecurrencePattern.WEEKLY to "أسبوعي (كل أسبوع)",
        RecurrencePattern.WEEKLY_THURSDAY to "كل خميس",
        RecurrencePattern.ONCE to "مرة واحدة فقط"
    )

    PrimaryDialog(onDismiss = onDismiss, backgroundColor = Theme.color.surfaces.surface) {
        Box(modifier = modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Theme.color.surfaces.surfaceHigh)
                    .clickable { onDismiss() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_close),
                    contentDescription = null,
                    tint = Theme.color.primary.primary,
                    modifier = Modifier.size(16.dp)
                )
            }

            Column(
                modifier = Modifier
                    .padding(top = 12.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "اختر نمط التكرار",
                    textAlign = TextAlign.Center,
                    style = Theme.textStyle.title.medium,
                    color = Theme.color.primary.shadePrimary
                )

                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    options.forEach { (pattern, label) ->
                        val isChecked = selected == pattern
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isChecked) Theme.color.primary.primary.copy(alpha = 0.08f) else Theme.color.surfaces.surfaceHigh)
                                .clickable { selected = pattern }
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = label,
                                style = Theme.textStyle.body.medium,
                                color = Theme.color.primary.shadePrimary
                            )

                            RadioButton(
                                selected = isChecked,
                                onClick = { selected = pattern },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = Theme.color.primary.primary,
                                    unselectedColor = Theme.color.secondary.secondaryText
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                PrimaryButton(
                    text = localizedString(R.string.save),
                    onClick = { onConfirm(selected) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
