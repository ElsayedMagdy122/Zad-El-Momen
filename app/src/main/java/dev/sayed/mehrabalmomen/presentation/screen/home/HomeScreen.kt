package dev.sayed.mehrabalmomen.presentation.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.navigation.Route
import dev.sayed.mehrabalmomen.presentation.screen.home.component.FeaturesSection
import dev.sayed.mehrabalmomen.presentation.screen.home.component.HomeAppBar
import dev.sayed.mehrabalmomen.presentation.screen.home.component.PrayersRowSection
import dev.sayed.mehrabalmomen.presentation.screen.home.component.UpComingPrayer
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = koinViewModel()
) {
    val state by viewModel.screenState.collectAsState()
    LaunchedEffect(Unit) {
        viewModel.effect.collect {
            when (it) {
                HomeEffect.NavigateToFullPrayersDetails -> {
                    navController.navigate(Route.FullPrayerTimeView)
                }

                HomeEffect.NavigateToCalibrateDevice -> {
                    navController.navigate(Route.CalibrateDevice)
                }
            }
        }
    }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.color.surfaces.surface)
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {
        item { HomeAppBar(modifier = Modifier.padding(horizontal = 16.dp), locationUiState = state.location) }
        item { UpComingPrayer(state = state, modifier = Modifier.padding(horizontal = 16.dp)) }
        item { PrayersRowSection(state.prayers, homeInteractionListener = viewModel) }
        item { FeaturesSection(homeInteractionListener = viewModel) }
    }
}