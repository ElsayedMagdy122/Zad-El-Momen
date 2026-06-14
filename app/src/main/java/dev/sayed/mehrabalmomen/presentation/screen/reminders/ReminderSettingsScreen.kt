package dev.sayed.mehrabalmomen.presentation.screen.reminders

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.component.AppBar
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.domain.model.ReminderConfig
import dev.sayed.mehrabalmomen.presentation.base.localizedString
import dev.sayed.mehrabalmomen.presentation.components.PrimaryDialog
import dev.sayed.mehrabalmomen.presentation.screen.reminders.components.ReminderItem
import dev.sayed.mehrabalmomen.presentation.screen.reminders.components.TimePickerDialog
import dev.sayed.mehrabalmomen.presentation.screen.reminders.components.TimeWheelPicker
import org.koin.androidx.compose.koinViewModel

@Composable
fun ReminderSettingsScreen(
    navController: NavController,
    viewModel: ReminderSettingsViewModel = koinViewModel()
) {

    val state by viewModel.screenState.collectAsState()

    var selectedReminder by remember {
        mutableStateOf<ReminderConfig?>(null)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.color.surfaces.surface)
            .windowInsetsPadding(WindowInsets.systemBars)
            .padding(horizontal = 16.dp),

        verticalArrangement = Arrangement.spacedBy(12.dp),

        contentPadding = PaddingValues(bottom = 16.dp)
    ) {

        item {

            AppBar(
                isBackEnabled = true,
                onBackClick = {
                    navController.popBackStack()
                },
                title = stringResource(R.string.notifications)
            )
        }

        items(state.reminders) { reminder ->

            ReminderItem(
                config = reminder,

                onToggle = { isChecked ->

                    viewModel.onToggleReminder(
                        reminder.type,
                        isChecked,
                        reminder.hour,
                        reminder.minute
                    )
                },

                onTimeClick = {
                    selectedReminder = reminder
                }
            )
        }
    }

    selectedReminder?.let { reminder ->

        TimePickerDialog(
            initialHour = reminder.hour,
            initialMinute = reminder.minute,

            onDismiss = {
                selectedReminder = null
            },

            onConfirm = { hour, minute ->

                viewModel.onTimeSelected(
                    reminder.type,
                    true,
                    hour,
                    minute
                )

                selectedReminder = null
            }
        )
    }
}


