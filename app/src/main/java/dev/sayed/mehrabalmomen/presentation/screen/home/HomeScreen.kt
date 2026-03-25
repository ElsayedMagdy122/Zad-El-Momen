package dev.sayed.mehrabalmomen.presentation.screen.home

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.base.LocalAppLocale
import dev.sayed.mehrabalmomen.presentation.navigation.Route
import dev.sayed.mehrabalmomen.presentation.screen.home.component.ContinueToTilawah
import dev.sayed.mehrabalmomen.presentation.screen.home.component.FeaturesSection
import dev.sayed.mehrabalmomen.presentation.screen.home.component.HomeAppBar
import dev.sayed.mehrabalmomen.presentation.screen.home.component.PrayerTimesCard
import dev.sayed.mehrabalmomen.presentation.utils.CollectEffect
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = koinViewModel()
) {
    val state by viewModel.screenState.collectAsStateWithLifecycle()
    RequestNotificationPermission()
    val language = LocalAppLocale.current
    LaunchedEffect(Unit) {
        viewModel.getHijriDate(language)
    }
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

            HomeEffect.NavigateToQuran -> {
                navController.navigate(Route.SurahListScreen)
            }

            HomeEffect.NavigateToTilawah -> {
                // TODO
            }
        }
    }
    LaunchedEffect(Unit) {
        viewModel.onScreenOpened()
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
                state = state,
            )
        }
        item {
            PrayerTimesCard(
                modifier = Modifier.padding(horizontal = 16.dp),
                prayerTimesUiState = state,
            )
        }
        item {
            ContinueToTilawah(
                onClick = {
                    navController.navigate(
                        Route.SurahAyatScreen(
                            surahId = state.lastTilawahUi.surahId,
                            arabicName = state.lastTilawahUi.nameArabic,
                            englishName = state.lastTilawahUi.nameEnglish,
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

@Composable
fun RequestNotificationPermission() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {

            } else {

            }
        }

        LaunchedEffect(Unit) {
            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}