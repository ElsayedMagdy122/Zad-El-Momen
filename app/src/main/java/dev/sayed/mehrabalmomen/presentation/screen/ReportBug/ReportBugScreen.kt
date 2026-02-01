package dev.sayed.mehrabalmomen.presentation.screen.ReportBug

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.component.PrimaryToast
import dev.sayed.mehrabalmomen.design_system.component.ToastDetails
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.screen.ReportBug.components.AttachmentsSection
import dev.sayed.mehrabalmomen.presentation.screen.ReportBug.components.BugTitleSection
import dev.sayed.mehrabalmomen.presentation.screen.ReportBug.components.DescriptionSection
import dev.sayed.mehrabalmomen.presentation.screen.ReportBug.components.FeatureAreaSection
import dev.sayed.mehrabalmomen.presentation.screen.ReportBug.components.ReportBugTopBar
import dev.sayed.mehrabalmomen.presentation.screen.ReportBug.components.SubmitButton
import dev.sayed.mehrabalmomen.presentation.utils.CollectEffect
import org.koin.androidx.compose.koinViewModel


@Composable
fun ReportBugScreen(navController: NavController, viewModel: ReportBugViewModel = koinViewModel()) {
    val state by viewModel.screenState.collectAsStateWithLifecycle()
    var toast by remember { mutableStateOf<ToastDetails?>(null) }
    CollectEffect(viewModel.effect) { effect ->
        when (effect) {
            is ReportBugEffect.Error -> {
                toast = ToastDetails(
                    title = R.string.error,
                    message = effect.message,
                    icon = R.drawable.ic_close_circle
                )
            }

            ReportBugEffect.InvalidInput -> {
                toast = ToastDetails(
                    title = R.string.invalid_input,
                    message = R.string.please_fill_all_required_fields,
                    icon = R.drawable.ic_close_circle
                )
            }

            ReportBugEffect.LimitReached -> {
                toast = ToastDetails(
                    title = R.string.daily_limit,
                    message = R.string.you_have_reached_the_daily_report_limit,
                    icon = R.drawable.ic_close_circle
                )
            }

            ReportBugEffect.Success -> {
                toast = ToastDetails(
                    title = R.string.success,
                    message = R.string.report_sent_successfully,
                    icon = R.drawable.ic_check_circle
                )
            }
        }
    }
    LaunchedEffect(toast) {
        toast?.let { current ->
            kotlinx.coroutines.delay(2000)
            if (toast == current) toast = null
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.color.surfaces.surface)
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp)
        ) {
            item { ReportBugTopBar(onBackClick = { navController.popBackStack() }) }
            item { BugTitleSection(value = state.title, onValueChange = viewModel::onTitleChange) }
            item {
                FeatureAreaSection(
                    selected = state.feature,
                    onSelect = viewModel::onFeatureSelected
                )
            }
            item {
                DescriptionSection(
                    value = state.description,
                    onValueChange = viewModel::onDescriptionChange
                )
            }
            item { AttachmentsSection(viewModel = viewModel, selectedImageUri = state.imageUrl) }
            item { SubmitButton(loading = state.isLoading, onClick = viewModel::onSubmitClick) }
        }

        toast?.let {
            PrimaryToast(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 24.dp),
                data = it,
                isSuccess = it.icon == R.drawable.ic_check_circle
            )
        }
    }
}
