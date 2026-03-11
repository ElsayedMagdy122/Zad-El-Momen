package dev.sayed.mehrabalmomen.presentation.screen.batteryOptimization

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.navigation.Route
import dev.sayed.mehrabalmomen.presentation.screen.batteryOptimization.components.BatteryInstructions
import dev.sayed.mehrabalmomen.presentation.screen.batteryOptimization.components.BatteryOptimizationActions
import dev.sayed.mehrabalmomen.presentation.screen.batteryOptimization.components.BatteryOptimizationHeader
import dev.sayed.mehrabalmomen.presentation.screen.batteryOptimization.components.HeaderBox
import dev.sayed.mehrabalmomen.presentation.screen.batteryOptimization.components.LearnMoreSection
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
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars)
            .background(Theme.color.surfaces.surface)
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .verticalScroll(scrollState)
    ) {
        BatteryOptimizationHeader(viewModel)

        BatteryOptimizationContent(
            listener = viewModel,
            instructions = uiState.instructions
        )
        Spacer(modifier = Modifier.weight(1f))
        BatteryOptimizationActions(listener = viewModel)
    }
}


@Composable
private fun BatteryOptimizationContent(
    listener: BatteryOptimizationInteractionListener,
    instructions: List<String>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        HeaderBox(modifier = Modifier.padding(top = 24.dp))
        BatteryInstructions(
            instructions = instructions,
            modifier = Modifier.padding(top = 16.dp)
        )
        LearnMoreSection(modifier = Modifier.padding(top = 24.dp), listener = listener)
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

            BatteryOptimizationEffect.SkipForNow ->
                navController.navigate(Route.LocationPermissionScreen)

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