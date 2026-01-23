package dev.sayed.mehrabalmomen.presentation.screen.SurahAyat

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.component.BottomSheetDs
import dev.sayed.mehrabalmomen.design_system.component.PrimaryToast
import dev.sayed.mehrabalmomen.design_system.component.ToastDetails
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.base.localizedString
import dev.sayed.mehrabalmomen.presentation.components.LoadingContainer
import dev.sayed.mehrabalmomen.presentation.navigation.Route
import dev.sayed.mehrabalmomen.presentation.screen.SearchAyah.SearchType
import dev.sayed.mehrabalmomen.presentation.screen.SurahAyat.components.AyaActionsSection
import dev.sayed.mehrabalmomen.presentation.screen.SurahAyat.components.BismillahSection
import dev.sayed.mehrabalmomen.presentation.screen.SurahAyat.components.QuranTextSection
import dev.sayed.mehrabalmomen.presentation.screen.SurahAyat.components.SurahAppBarSection
import dev.sayed.mehrabalmomen.presentation.utils.CollectEffect
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun SurahAyatScreen(
    surahId: Int,
    surahName: String,
    navController: NavController,
    viewModel: SurahAyatViewModel = koinViewModel()
) {
    val clipboardManager = LocalClipboardManager.current
    val state by viewModel.screenState.collectAsStateWithLifecycle()
    var toast by remember { mutableStateOf<ToastDetails?>(null) }
    CollectEffect(viewModel.effect) { effect ->
        when (effect) {
            is SurahAyatEffect.ShowToast -> toast = effect.toast
            is SurahAyatEffect.CopyAya -> {
                clipboardManager.setText(
                    AnnotatedString(effect.text)
                )
            }

            SurahAyatEffect.NavigateToBack -> {
                navController.popBackStack()
            }

            is SurahAyatEffect.NavigateToSearch -> {
                navController.navigate(
                    Route.SearchAyahScreen(
                        type = SearchType.SURAH,
                        surahId = effect.surahId,
                        surahName = effect.surahName
                    )
                )
            }
        }

    }
    LaunchedEffect(toast) {
        toast?.let {
            val current = it
            delay(2000)
            if (toast == current) {
                toast = null
            }
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.color.surfaces.surface)
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {
        AnimatedContent(
            targetState = state.isLoading,
            transitionSpec = {

                fadeIn(animationSpec = tween(400)) + scaleIn(initialScale = 0.92f) togetherWith
                        fadeOut(animationSpec = tween(300))
            },
            label = "LoadingToContentAnimation"
        ) { isLoading ->
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    LoadingContainer()
                }
            } else {
                SurahAyatContent(
                    state = state,
                    surahId = surahId,
                    listener = viewModel,
                    viewModel = viewModel
                )
            }
        }

        toast?.let {
            PrimaryToast(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 24.dp), data = it, isSuccess = true
            )
        }
        state.showTafseerSheet.takeIf { it }?.let {
            TafseerBottomSheet(
                tafseerUi = state.tafseerUi,
                surahName = state.surahName,
                onDismiss = viewModel::onDismissTafseerSheet
            )
        }
    }
}

@Composable
private fun SurahAyatContent(
    state: SurahAyatUiState,
    surahId: Int,
    listener: SurahAyatInteractionListener,
    viewModel: SurahAyatViewModel
) {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val textSectionIndex = if (surahId != 1 && surahId != 9) 2 else 1
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
            stickyHeader {
                SurahAppBarSection(
                    surahName = state.surahName,
                    onBack = listener::onClickBack,
                    onSearch = listener::onClickSearch
                )
            }

            if (surahId != 1 && surahId != 9) {
                item {
                    BismillahSection(color = Theme.color.primary.primary)
                }
            }

            item {
                QuranTextSection(
                    state = state,
                    onAyaLongPressed = listener::onAyaLongPressed,
                    onClearSelection = listener::onClearSelection,
                    onCalculatedPosition = { yOffset ->
                        if (state.targetAyahId != null) {
                            scope.launch {
                                val finalOffset = yOffset.toInt() - 80

                                listState.animateScrollToItem(
                                    index = textSectionIndex,
                                    scrollOffset = if (finalOffset > 0) finalOffset else 0
                                )
                                viewModel.onScrolledToTarget()
                            }
                        }
                    }
                )
            }
        }

        AyaActionsSection(
            showActions = state.showActions,
            selectedAyaText = state.selectedAyaText,
            onCopy = listener::onCopyAya,
            onBookmark ={/* TODO */},
            onTafseer =listener::onTafseer
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TafseerBottomSheet(
    surahName: String,
    tafseerUi: TafseerUi?,
    onDismiss: () -> Unit
) {
    if (tafseerUi == null) return

    BottomSheetDs(onDismiss = onDismiss) {

        Text(
            text = localizedString(R.string.al_tafseer),
            style = Theme.textStyle.title.medium,
            color = Theme.color.primary.primary
        )

        Spacer(modifier = Modifier.height(8.dp))


        tafseerUi.ayahUi?.let {
            Text(
                text = "سورة $surahName · ${localizedString(R.string.ayah)} ${it.id}",
                style = Theme.textStyle.title.small,
                color = Theme.color.semantic.shadeTertiary
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = tafseerUi.text.orEmpty(),
            style = Theme.textStyle.title.small,
            color = Theme.color.semantic.shadeTertiary
        )
    }
}

