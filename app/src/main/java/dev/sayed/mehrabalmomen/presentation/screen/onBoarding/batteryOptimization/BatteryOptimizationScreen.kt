package dev.sayed.mehrabalmomen.presentation.screen.onBoarding.batteryOptimization

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.navigation.Route
import dev.sayed.mehrabalmomen.presentation.screen.onBoarding.batteryOptimization.components.BatteryInstructions
import dev.sayed.mehrabalmomen.presentation.screen.onBoarding.batteryOptimization.components.BatteryOptimizationActions
import dev.sayed.mehrabalmomen.presentation.screen.onBoarding.batteryOptimization.components.BatteryOptimizationHeader
import dev.sayed.mehrabalmomen.presentation.screen.onBoarding.batteryOptimization.components.HeaderBox
import dev.sayed.mehrabalmomen.presentation.screen.onBoarding.batteryOptimization.components.LearnMoreSection
import dev.sayed.mehrabalmomen.presentation.utils.CollectEffect
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun BatteryOptimizationScreen(
    navController: NavController,
    viewModel: BatteryOptimizationViewModel = koinViewModel()
) {
    val manufacturer = Build.MANUFACTURER.lowercase()
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val uiState by viewModel.screenState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.loadInstructions(manufacturer, isRtl)
    }

    HandleEffects(viewModel = viewModel, navController = navController, context = context)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars)
            .background(Theme.color.surfaces.surface)
            .padding(horizontal = 16.dp)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 120.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                BatteryOptimizationHeader(viewModel)
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                HeaderBox()
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                BatteryInstructions(
                    instructions = uiState.instructions
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                LearnMoreSection(listener = viewModel)
            }
        }

        BatteryOptimizationActions(
            listener = viewModel,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
        )
    }
}


@Composable
private fun HandleEffects(
    viewModel: BatteryOptimizationViewModel,
    navController: NavController,
    context: Context
) {
    CollectEffect(viewModel.effect) {
        when (it) {
            BatteryOptimizationEffect.NavigateBack ->
                navController.popBackStack()

            BatteryOptimizationEffect.OpenSettings ->
                context.openAppSettings()

            BatteryOptimizationEffect.NavigateToHome -> {
                navController.navigate(Route.AppRoute) {
                    popUpTo(Route.CalculationMethodScreen) { inclusive = true }
                }
            }

            BatteryOptimizationEffect.NavigateToLearnMore ->
                context.openUrl("https://dontkillmyapp.com")
        }
    }
}

private fun Context.openAppSettings() {
    startActivity(
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
        }
    )
}

private fun Context.openUrl(url: String) {
    runCatching {
        startActivity(
            Intent(Intent.ACTION_VIEW, Uri.parse(url))
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }
}