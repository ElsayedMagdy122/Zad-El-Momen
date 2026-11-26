package dev.sayed.mehrabalmomen.presentation.screen.home

import androidx.compose.runtime.Composable

@Composable
expect fun AlarmPermissionHandler(
    viewModel: HomeViewModel,
    state: HomeUiState
)
