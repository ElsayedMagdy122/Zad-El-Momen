package dev.sayed.mehrabalmomen.presentation.screen.qiblah

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.design_system.component.AppBar
import dev.sayed.mehrabalmomen.presentation.base.localizedString
import dev.sayed.mehrabalmomen.presentation.screen.qiblah.components.DirectionCard
import dev.sayed.mehrabalmomen.presentation.screen.qiblah.components.KaabaOnCircle
import dev.sayed.mehrabalmomen.presentation.utils.CompassSensorHandler
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun QiblahScreen(
    navController: NavController,
    viewModel: QiblahViewModel = koinViewModel()
) {
    val state by viewModel.screenState.collectAsStateWithLifecycle()
    val animatedDirection by animateFloatAsState(targetValue = state.direction)

    CompassSensorHandler(onDirectionChanged = { viewModel.updateDirection(it) })
    LaunchedEffect(Unit) {
        viewModel.onScreenOpened()
    }
    QiblahScreenContent(
        navController = navController,
       state = state
    )

}

@Composable
private fun QiblahScreenContent(
    navController: NavController,
    state: QiblahUiState
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.color.surfaces.surface)
            .windowInsetsPadding(WindowInsets.systemBars)
            .padding(horizontal = 16.dp)
            .verticalScroll(state = rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        AppBar(
            onBackClick = { navController.popBackStack() },
            title = localizedString(R.string.qiblah),
        )

        KaabaOnCircle(
            directionDegrees = state.direction,
            modifier = Modifier.padding(vertical = 64.dp)
        )

        DirectionCard(locationUiState = state.location,direction =state.direction)
    }
}