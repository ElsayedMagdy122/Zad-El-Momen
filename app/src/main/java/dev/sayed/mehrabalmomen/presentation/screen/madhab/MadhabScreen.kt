package dev.sayed.mehrabalmomen.presentation.screen.madhab

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.component.AppBar
import dev.sayed.mehrabalmomen.design_system.component.PrimaryButton
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.base.localizedString
import dev.sayed.mehrabalmomen.presentation.components.CheckboxItem
import dev.sayed.mehrabalmomen.presentation.navigation.Route
import dev.sayed.mehrabalmomen.presentation.utils.CollectEffect
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MadhabScreen(
    navController: NavController,
    viewModel: MadhabViewModel = koinViewModel()
) {
    val state by viewModel.screenState.collectAsStateWithLifecycle()
    CollectEffect(viewModel.effect) { effect ->
        when (effect) {
            MadhabEffect.NavigateToCalculationMethod -> {
                navController.navigate(Route.CalculationMethodScreen)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.color.surfaces.surface)
            .windowInsetsPadding(WindowInsets.systemBars)
            .padding(horizontal = 16.dp)
    ) {

        LazyVerticalGrid(
            modifier = Modifier
                .align(Alignment.TopCenter),
            columns = GridCells.Adaptive(minSize = 320.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                AppBar(
                    title = localizedString(R.string.juristic_madhab),
                    onBackClick = {},
                    isBackEnabled = false
                )
            }
            items(MadhabUiState.MadhabState.entries) { madhab ->
                CheckboxItem(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    text = localizedString(madhab.value),
                    isChecked = state.selectedMadhab == madhab,
                    onCheckedChange = {
                        viewModel.onMadhabClicked(madhab)
                    }
                )
            }
        }
        PrimaryButton(
            isLoading = false,
            isEnabled = true,
            text = localizedString(R.string.btn_continue),
            onClick = { viewModel.onClickContinue() },
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
        )
    }
}