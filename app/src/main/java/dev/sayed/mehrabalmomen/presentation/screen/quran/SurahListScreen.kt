package dev.sayed.mehrabalmomen.presentation.screen.quran

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.component.AppBar
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.base.localizedString
import dev.sayed.mehrabalmomen.presentation.navigation.Route
import org.koin.androidx.compose.koinViewModel
import dev.sayed.mehrabalmomen.presentation.screen.quran.components.SurahGrid
@Composable
fun SurahListScreen(
    navController: NavController,
    viewModel: SurahListViewModel = koinViewModel()
) {
    val state by viewModel.screenState.collectAsState()
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is SurahListEffect.NavigateToSurahAyat ->
                    navController.navigate(Route.SurahAyatScreen(effect.surahId,effect.surahName))
            }
        }
    }

    SurahListContent(
        state = state,
        listener = viewModel,
        onBack = navController::popBackStack
    )
}

@Composable
private fun SurahListContent(
    state: SurahListUiState,
    listener: SurahListInteractionListener,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.color.surfaces.surface)
            .windowInsetsPadding(WindowInsets.systemBars)
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AppBar(
            onBackClick = onBack,
            title = localizedString(R.string.quran),
        )

        SurahGrid(
            sur = state.surahList,
            onSurahClick = listener::onSurahClick
        )
    }
}