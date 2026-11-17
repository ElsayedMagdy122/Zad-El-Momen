package dev.sayed.mehrabalmomen.presentation.screen.radio

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import org.koin.compose.viewmodel.koinViewModel

@Composable
expect fun RadioScreen(
    navController: NavController,
    viewModel: RadioChannelsViewModel = koinViewModel()
)
