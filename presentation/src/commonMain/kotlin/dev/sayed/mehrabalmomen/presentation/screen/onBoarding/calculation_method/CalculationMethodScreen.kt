package dev.sayed.mehrabalmomen.presentation.screen.onBoarding.calculation_method

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import dev.sayed.mehrabalmomen.design_system.component.AppBar
import dev.sayed.mehrabalmomen.design_system.component.PrimaryButton
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.components.CheckboxItem
import dev.sayed.mehrabalmomen.presentation.utils.CollectEffect
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import zad_el_momen.presentation.generated.resources.Res
import zad_el_momen.presentation.generated.resources.*

@Composable
fun CalculationMethodScreen(
    onNavigateToPermissions: () -> Unit,
    viewModel: CalculationMethodViewModel = koinViewModel()
) {
    val state by viewModel.screenState.collectAsStateWithLifecycle()
    CollectEffect(viewModel.effect) { effect ->
        when (effect) {
            CalculationMethodEffect.NavigateToPermissionsScreen -> {
                onNavigateToPermissions()
            }
        }
    }
    val bottomPadding = 24.dp + 56.dp
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.color.surfaces.surface)
            .windowInsetsPadding(WindowInsets.systemBars)
            .padding(horizontal = 16.dp)
    ) {

        LazyVerticalGrid(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.TopCenter),
            columns = GridCells.Adaptive(minSize = 320.dp),
            contentPadding = PaddingValues(
                top = 8.dp,
                bottom = bottomPadding
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item(span = { GridItemSpan(maxLineSpan) }){
                AppBar(
                    title = stringResource(Res.string.prayer_time_calculation_methods),
                    onBackClick = {},
                    isBackEnabled = false
                )
            }
            items(CalculationMethodUiState.CalculationMethod.entries) { method ->
                CheckboxItem(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    text = stringResource(method.res),
                    isChecked = state.selectedMethod == method,
                    onCheckedChange = {
                        viewModel.onCalculationMethodClicked(method)
                    }
                )
            }
        }
        PrimaryButton(
            isLoading = false,
            isEnabled = true,
            text = stringResource(Res.string.btn_continue),
            onClick = { viewModel.onClickContinue() },
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
        )
    }
}
