package dev.sayed.mehrabalmomen.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.theme.MehrabTheme
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.base.localizedString

@Composable
fun SingleSelectionDialog(
    title: Int,
    items: List<SelectionItem>,
    selectedIndex: Int = -1,
    onItemSelected: (Int) -> Unit = {},
    onConfirm: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    var currentSelected by remember { mutableStateOf(selectedIndex) }

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(Theme.color.surfaces.surfaceHigh)
                .padding(16.dp)
                .widthIn(max = 400.dp)
        ) {
            Text(
                text = localizedString(title),
                style = Theme.textStyle.title.medium,
                color = Theme.color.primary.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f, fill = false)
            ) {

                items.forEachIndexed { index, item ->
                    CheckboxItem(
                        text = localizedString( item.text),
                        description = item.description,
                        icon = item.icon?.let { painterResource(it) },
                        isChecked = currentSelected == index,
                        onCheckedChange = { checked ->
                            if (checked) {
                                currentSelected = index
                                onItemSelected(index)
                            }
                        },
                        backgroundColor = Theme.color.surfaces.surfaceLow
                    )

                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onDismiss) {
                    Text(
                       text = "Cancel",
                        color = Theme.color.secondary.shadeSecondary
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(
                    onClick = {
                        if (currentSelected != -1) {
                            onConfirm(currentSelected)
                        }
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Theme.color.primary.primary,
                        contentColor = Color.White
                    )
                ) {
                    Text(text = localizedString(R.string.confirm), color = Theme.color.primary.onPrimary)
                }
            }
        }
    }
}

data class SelectionItem(
    val text: Int,
    val description: String? = null,
    val icon: Int? = null
)

@Preview(showBackground = true)
@Composable
private fun SingleSelectionDialogPreview() {
    var showDialog by remember { mutableStateOf(true) }
    var selectedMadhab by remember { mutableStateOf(0) }

    val madhabs = listOf(
        SelectionItem(R.string.shafi),
        SelectionItem(R.string.hanafi)
    )

    MehrabTheme(isDarkTheme = false) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Button(onClick = { showDialog = true }) {
                Text("Show Select Madhab Dialog")
            }

            if (showDialog) {
                SingleSelectionDialog(
                    title = R.string.select_madhab,
                    items = madhabs,
                    selectedIndex = selectedMadhab,
                    onConfirm = { index ->
                        selectedMadhab = index
                    },
                    onDismiss = { showDialog = false }
                )
            }
        }
    }
}
