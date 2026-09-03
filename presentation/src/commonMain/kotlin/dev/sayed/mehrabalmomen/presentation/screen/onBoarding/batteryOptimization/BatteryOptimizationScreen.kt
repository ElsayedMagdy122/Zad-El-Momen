package dev.sayed.mehrabalmomen.presentation.screen.onBoarding.batteryOptimization

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
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.screen.onBoarding.batteryOptimization.components.BatteryInstructions
import dev.sayed.mehrabalmomen.presentation.screen.onBoarding.batteryOptimization.components.BatteryOptimizationActions
import dev.sayed.mehrabalmomen.presentation.screen.onBoarding.batteryOptimization.components.BatteryOptimizationHeader
import dev.sayed.mehrabalmomen.presentation.screen.onBoarding.batteryOptimization.components.HeaderBox
import dev.sayed.mehrabalmomen.presentation.screen.onBoarding.batteryOptimization.components.LearnMoreSection
import dev.sayed.mehrabalmomen.presentation.utils.CollectEffect
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun BatteryOptimizationScreen(
    onNavigateBack: () -> Unit,
    onOpenSettings: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToLearnMore: () -> Unit,
    manufacturer: String,
    viewModel: BatteryOptimizationViewModel = koinViewModel()
) {
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val uiState by viewModel.screenState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadInstructions(manufacturer, isRtl)
    }

    CollectEffect(viewModel.effect) {
        when (it) {
            BatteryOptimizationEffect.NavigateBack -> onNavigateBack()
            BatteryOptimizationEffect.OpenSettings -> onOpenSettings()
            BatteryOptimizationEffect.NavigateToHome -> onNavigateToHome()
            BatteryOptimizationEffect.NavigateToLearnMore -> onNavigateToLearnMore()
        }
    }

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
