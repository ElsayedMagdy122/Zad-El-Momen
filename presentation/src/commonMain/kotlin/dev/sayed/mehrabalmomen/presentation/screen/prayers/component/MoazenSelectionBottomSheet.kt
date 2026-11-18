package dev.sayed.mehrabalmomen.presentation.screen.prayers.component

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.design_system.component.BottomSheetDs
import dev.sayed.mehrabalmomen.design_system.component.PrimaryButton
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.base.localizedString
import dev.sayed.mehrabalmomen.presentation.components.CheckboxItem
import dev.sayed.mehrabalmomen.presentation.screen.settings.SelectionItem
import org.jetbrains.compose.resources.painterResource
import zadelmomen.presentation.generated.resources.Res
import zadelmomen.presentation.generated.resources.choose_moazen
import zadelmomen.presentation.generated.resources.confirm
import zadelmomen.presentation.generated.resources.ic_pause
import zadelmomen.presentation.generated.resources.ic_play
import zadelmomen.presentation.generated.resources.moazen_description

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoazenSelectionBottomSheet(
    items: List<SelectionItem>,
    selectedIndex: Int,
    onConfirm: (Int) -> Unit,
    onDismiss: () -> Unit,
    playSound: (Int) -> Unit,
    stopSound: () -> Unit
) {
    var currentSelected by remember { mutableStateOf(selectedIndex) }
    var currentlyPlayingIndex by remember { mutableStateOf<Int?>(null) }

    BottomSheetDs(onDismiss = {
        stopSound()
        onDismiss()
    }) {
        Text(
            text = localizedString(Res.string.choose_moazen),
            style = Theme.textStyle.label.medium,
            color = Theme.color.primary.shadePrimary
        )
        Text(
            text = localizedString(Res.string.moazen_description),
            style = Theme.textStyle.label.medium,
            color = Theme.color.semantic.shadeTertiary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items.forEachIndexed { index, item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Theme.color.surfaces.surfaceLow)
                        .clickable { currentSelected = index }
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Theme.color.surfaces.surfaceHigh)
                            .clickable {
                                if (currentlyPlayingIndex == index) {
                                    stopSound()
                                    currentlyPlayingIndex = null
                                } else {
                                    stopSound()
                                    playSound(index)
                                    currentlyPlayingIndex = index
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(
                                if (currentlyPlayingIndex == index) Res.drawable.ic_pause else Res.drawable.ic_play
                            ),
                            contentDescription = null,
                            tint = Theme.color.primary.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    CheckboxItem(
                        text = item.text?.asString() ?: "",
                        description = item.description,
                        isChecked = currentSelected == index,
                        onCheckedChange = { checked ->
                            if (checked) currentSelected = index
                        },
                        backgroundColor = Theme.color.surfaces.surfaceLow,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            PrimaryButton(
                modifier = Modifier.fillMaxWidth(),
                text = localizedString(Res.string.confirm),
                onClick = {
                    stopSound()
                    onConfirm(currentSelected)
                    onDismiss()
                }
            )
        }
    }
}
