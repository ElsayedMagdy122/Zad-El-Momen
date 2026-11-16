package dev.sayed.mehrabalmomen.presentation.screen.onBoarding.permissions

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import org.koin.compose.viewmodel.koinViewModel

@Composable
expect fun PermissionsScreen(
    navController: NavController,
    viewModel: PermissionsViewModel = koinViewModel()
)
