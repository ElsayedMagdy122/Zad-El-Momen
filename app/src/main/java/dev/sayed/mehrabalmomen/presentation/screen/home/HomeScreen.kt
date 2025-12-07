package dev.sayed.mehrabalmomen.presentation.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.DateCard
import dev.sayed.mehrabalmomen.UpComingPrayer
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.screen.home.component.HomeAppBar
import dev.sayed.mehrabalmomen.presentation.screen.home.component.PrayersRowSection
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(viewModel: HomeViewModel = koinViewModel()) {
    val state by viewModel.screenState.collectAsState()
    println("SAYED : HomeScreen ${state.prayers} ")
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.color.surfaces.surface)
            .windowInsetsPadding(WindowInsets.statusBars)
            .scrollable(rememberScrollState(), orientation = Orientation.Vertical)
    ) {
        HomeAppBar(modifier = Modifier.padding(horizontal = 16.dp))
        UpComingPrayer(modifier = Modifier.padding(horizontal = 16.dp))
        PrayersRowSection(state.prayers)
    }
}