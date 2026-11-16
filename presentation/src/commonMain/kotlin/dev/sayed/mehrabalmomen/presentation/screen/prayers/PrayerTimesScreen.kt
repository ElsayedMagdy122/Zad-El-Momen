package dev.sayed.mehrabalmomen.presentation.screen.prayers

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import org.koin.compose.viewmodel.koinViewModel

@Composable
expect fun PrayerTimesScreen(
    navController: NavController,
    viewModel: PrayerTimesViewModel = koinViewModel()
)
