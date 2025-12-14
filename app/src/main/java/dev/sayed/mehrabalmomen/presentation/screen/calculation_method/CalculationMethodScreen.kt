package dev.sayed.mehrabalmomen.presentation.screen.calculation_method

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.component.AppBar
import dev.sayed.mehrabalmomen.design_system.component.PrimaryButton
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.components.CheckboxItem
import dev.sayed.mehrabalmomen.presentation.navigation.Route
import org.koin.androidx.compose.koinViewModel

@Composable
fun CalculationMethodScreen(
    navController: NavController,
    viewModel: CalculationMethodViewModel = koinViewModel()
) {
    val state by viewModel.screenState.collectAsState()
    LaunchedEffect(Unit) {
        viewModel.effect.collect { it ->
            when (it) {
                CalculationMethodEffect.NavigateToLocationScreen -> {
                    navController.navigate(Route.LocationPermissionScreen)
                }
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
            stickyHeader {
                AppBar(
                    title = stringResource(R.string.prayer_time_calculation_methods),
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }
            items(CalculationMethodUiState.CalculationMethod.entries) { method ->
                CheckboxItem(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    text = stringResource( method.value),
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
            text =stringResource(R.string.btn_continue),
            onClick = { viewModel.onClickContinue() },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
        )
    }
}