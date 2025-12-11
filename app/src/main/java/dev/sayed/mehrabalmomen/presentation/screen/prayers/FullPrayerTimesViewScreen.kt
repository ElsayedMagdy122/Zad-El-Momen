package dev.sayed.mehrabalmomen.presentation.screen.prayers

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
import dev.sayed.mehrabalmomen.presentation.components.AppBar
import dev.sayed.mehrabalmomen.presentation.navigation.Route
import dev.sayed.mehrabalmomen.presentation.screen.home.HomeEffect
import dev.sayed.mehrabalmomen.presentation.screen.prayers.component.PrayerItems
import dev.sayed.mehrabalmomen.presentation.screen.prayers.component.UpComingPrayerFullView
import org.koin.androidx.compose.koinViewModel
import kotlin.time.ExperimentalTime

@ExperimentalTime
@Composable
fun FullPrayerTimesViewScreen(
    navController: NavController,
    viewModel: FullPrayerTimesViewModel = koinViewModel()) {
    val state by viewModel.screenState.collectAsState()
    LaunchedEffect(Unit) {
        viewModel.effect.collect {
           when(it){
               FullPrayerTimesEffect.NavigateBack ->{
                   navController.popBackStack()
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
        item {
            AppBar(
                onBackClick = viewModel::onClickBack,
                title = "Prayer Times",
                modifier = Modifier.padding(
                    horizontal = 16.dp
                )
            )
        }
        item {
            UpComingPrayerFullView(
                state = state,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
        item {
            PrayerItems(
                prayers = state.prayers,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }
}