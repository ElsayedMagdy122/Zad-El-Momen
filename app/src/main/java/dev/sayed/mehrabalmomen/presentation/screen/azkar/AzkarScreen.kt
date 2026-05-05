package dev.sayed.mehrabalmomen.presentation.screen.azkar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.component.AppBar
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.base.localizedString
import dev.sayed.mehrabalmomen.presentation.navigation.Route
import dev.sayed.mehrabalmomen.presentation.screen.home.component.FeatureCard
import dev.sayed.mehrabalmomen.presentation.utils.CollectEffect
import org.koin.androidx.compose.koinViewModel

@Composable
fun AzkarScreen(
    navController: NavController,
    viewModel: AzkarViewModel = koinViewModel()
) {

    val state by viewModel.screenState.collectAsStateWithLifecycle()
    CollectEffect(viewModel.effect) { effect ->
        when (effect) {
            is AzkarEffect.NavigateToDetails ->
                navController.navigate(
                    Route.AzkarDetailScreen(effect.type.domainTitle)
                )

            is AzkarEffect.ShowError -> {

            }

            AzkarEffect.NavigateToBack -> {
                navController.popBackStack()
            }
        }
    }
    LaunchedEffect(Unit) {
        viewModel.onScreenOpened()
    }
    LazyVerticalGrid(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.color.surfaces.surface)
            .windowInsetsPadding(WindowInsets.systemBars)
            .padding(horizontal = 16.dp),
        columns = GridCells.Adaptive(150.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            AppBar(
                isBackEnabled = false,
                onBackClick = viewModel::onClickBack,
                title = localizedString(R.string.azkar)
            )
        }

        items(state.categories) { category ->
            FeatureCard(
                icon = painterResource(category.type.iconRes),
                title = localizedString(category.type.titleRes),
                onClick = {
                    viewModel.onClickCategory(category.type)
                }
            )
        }
    }
}