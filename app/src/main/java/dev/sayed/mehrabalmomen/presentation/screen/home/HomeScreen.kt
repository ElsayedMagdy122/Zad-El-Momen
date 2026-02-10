package dev.sayed.mehrabalmomen.presentation.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.navigation.Route
import dev.sayed.mehrabalmomen.presentation.screen.home.component.ContinueToTilawah
import dev.sayed.mehrabalmomen.presentation.screen.home.component.FeaturesSection
import dev.sayed.mehrabalmomen.presentation.screen.home.component.HomeAppBar
import dev.sayed.mehrabalmomen.presentation.screen.home.component.PrayersRowSection
import dev.sayed.mehrabalmomen.presentation.screen.home.component.UpComingPrayer
import dev.sayed.mehrabalmomen.presentation.utils.CollectEffect
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = koinViewModel()
) {
    val state by viewModel.screenState.collectAsStateWithLifecycle()
    val countdownTime by viewModel.countdownTime.collectAsStateWithLifecycle()
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val surahName = if (isRtl) state.lastTilawahUi.nameArabic else state.lastTilawahUi.nameEnglish
    CollectEffect(viewModel.effect) { effect ->
        when (effect) {
            HomeEffect.NavigateToFullPrayersDetails -> {
                navController.navigate(Route.FullPrayerTimeView)
            }

            HomeEffect.NavigateToCalibrateDevice -> {
                navController.navigate(Route.CalibrateDevice)
            }

            HomeEffect.NavigateToSettings -> {
                navController.navigate(Route.SettingsScreen)
            }

            HomeEffect.NavigateToAzkar -> {
                navController.navigate(Route.AzkarScreen)
            }

            HomeEffect.NavigateToQuran -> {
                navController.navigate(Route.SurahListScreen)
            }

            HomeEffect.NavigateToTilawah -> {
                // TODO
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
            HomeAppBar(
                modifier = Modifier.padding(horizontal = 16.dp),
                locationUiState = state.location,
                onClickSettings = viewModel::onClickSettings
            )
        }
        item {
            UpComingPrayer(
                state = state,
                countdownTime = countdownTime,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
        item { PrayersRowSection(state.prayers, homeInteractionListener = viewModel) }
        item {
            ContinueToTilawah(
                onClick = {
                    navController.navigate(
                        Route.SurahAyatScreen(
                            surahId = state.lastTilawahUi.surahId,
                            surahName = surahName,
                            targetAyahId = state.lastTilawahUi.ayahId
                        )
                    )
                },
                surahUiState = state.lastTilawahUi,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(top = 8.dp)
            )
        }
        item { FeaturesSection(homeInteractionListener = viewModel) }
    }
}