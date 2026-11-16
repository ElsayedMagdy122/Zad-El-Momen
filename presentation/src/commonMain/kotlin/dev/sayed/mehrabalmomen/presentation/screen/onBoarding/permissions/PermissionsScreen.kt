package dev.sayed.mehrabalmomen.presentation.screen.onBoarding.permissions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dev.sayed.mehrabalmomen.design_system.component.PrimaryButton
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.base.localizedString
import dev.sayed.mehrabalmomen.presentation.navigation.Route
import dev.sayed.mehrabalmomen.presentation.utils.CollectEffect
import org.koin.compose.viewmodel.koinViewModel
import zadelmomen.presentation.generated.resources.Res
import zadelmomen.presentation.generated.resources.next

@Composable
fun PermissionsScreen(
    navController: NavController,
    viewModel: PermissionsViewModel = koinViewModel()
) {
    CollectEffect(viewModel.effect) { effect ->
        when (effect) {
            PermissionsEffect.NavigateToHome -> {
                navController.navigate(Route.AppRoute.route) {
                    popUpTo(Route.CalculationMethodScreen.route) { inclusive = true }
                }
            }
            else -> {}
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.color.surfaces.surface)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        PrimaryButton(
            isLoading = false,
            isEnabled = true,
            text = localizedString(Res.string.next),
            onClick = { viewModel.onClickNext() },
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
        )
    }
}
