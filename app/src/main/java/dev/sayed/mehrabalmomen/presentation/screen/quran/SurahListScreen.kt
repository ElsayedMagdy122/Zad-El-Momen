package dev.sayed.mehrabalmomen.presentation.screen.quran

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.base.localizedString
import dev.sayed.mehrabalmomen.presentation.components.AppBarAction
import dev.sayed.mehrabalmomen.presentation.components.QuranAppBar
import dev.sayed.mehrabalmomen.presentation.navigation.Route
import dev.sayed.mehrabalmomen.presentation.navigation.Route.*
import dev.sayed.mehrabalmomen.presentation.screen.SearchAyah.SearchType
import dev.sayed.mehrabalmomen.presentation.screen.quran.components.SurahGrid
import dev.sayed.mehrabalmomen.presentation.utils.CollectEffect
import org.koin.androidx.compose.koinViewModel

@Composable
fun SurahListScreen(
    navController: NavController,
    viewModel: SurahListViewModel = koinViewModel()
) {
    val state by viewModel.screenState.collectAsStateWithLifecycle()
    CollectEffect(viewModel.effect) { effect ->
        when (effect) {
            is SurahListEffect.NavigateToSurahAyat ->
                navController.navigate(
                    SurahAyatScreen(
                        effect.surahId,
                        effect.surahName
                    )
                )

            SurahListEffect.NavigateToQuranSearch -> {
                navController.navigate(
                    Route.SearchAyahScreen(
                        type = SearchType.QURAN
                    )
                )
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
        QuranAppBar(
            onBackClick = onBack,
            titleColor = Theme.color.primary.shadePrimary,
            title = localizedString(R.string.quran), actions = listOf(
                AppBarAction(
                    icon = painterResource(R.drawable.ic_search),
                    onClick = {
                        listener.onSearchClick()
                    },
                    ),
                AppBarAction(
                    icon = painterResource(R.drawable.ic_all_bookmark),
                    onClick = {},
                )
            )
        )
        SurahGrid(
            sur = state.surahList,
            onSurahClick = listener::onSurahClick
        )
    }
}