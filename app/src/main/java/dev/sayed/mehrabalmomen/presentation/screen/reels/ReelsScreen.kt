package dev.sayed.mehrabalmomen.presentation.screen.reels

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.component.AppBar
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.screen.reels.components.ReelItemCard
import dev.sayed.mehrabalmomen.presentation.utils.CollectEffect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ReelsScreen(
    navController: NavHostController,
    viewModel: ReelsViewModel = koinViewModel(),
) {
    val state   by viewModel.screenState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    CollectEffect(viewModel.effect) { effect ->
        when (effect) {
            is ReelsEffect.ShareReel -> {
                    val shared = runCatching {
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type    = "video/mp4"
                            putExtra(Intent.EXTRA_STREAM, effect.cachedReelUrl.toUri())
                            putExtra(Intent.EXTRA_SUBJECT, effect.title)
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }
                        context.startActivity(Intent.createChooser(intent, null))
                    }.isSuccess
                if (shared)
                    viewModel.onShareCompleted(effect.reelId,shared)
            }
            ReelsEffect.NavigateBack -> navController.navigateUp()
        }
    }

    ReelsContent(
        state               = state,
        interactionListener = viewModel,
    )
}

@Composable
private fun ReelsContent(
    state: ReelsUiState,
    interactionListener: ReelsInteractionListener,
) {
    val pagerState = rememberPagerState(pageCount = { state.reels.size })
    val pool       = rememberVideoPlayerPool()
    val urls       = remember(state.reels) { state.reels.map { it.videoUrl } }

    LaunchedEffect(urls) {
        if (urls.isNotEmpty()) pool.onPageChanged(0, urls)
    }

    var lastPage by remember { mutableIntStateOf(0) }

    LaunchedEffect(pagerState, urls) {
        snapshotFlow { pagerState.settledPage }
            .distinctUntilChanged()
            .filter { urls.isNotEmpty() }
            .collect { page ->
                if (page != lastPage) {
                    pool.rotate((page - lastPage).coerceIn(-1, 1))
                    lastPage = page
                }
                pool.onPageChanged(page, urls)
                if (page >= urls.size - 3) interactionListener.onLoadNextPage()
            }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner, pool) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE  -> pool.pauseActive()
                Lifecycle.Event.ON_RESUME -> pool.resumeActive()
                else                      -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val readyPages by pool.readyPages

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
    ) {
        when {
            state.isLoading -> CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color    = Theme.color.brand.brand,
            )

            state.reels.isNotEmpty() -> VerticalPager(
                state                   = pagerState,
                modifier                = Modifier.fillMaxSize(),
                beyondViewportPageCount = 2,
            ) { page ->
                ReelItemCard(
                    item                = state.reels[page],
                    player              = pool.playerForPage(page, pagerState.currentPage),
                    isActive            = page == pagerState.currentPage,
                    isReady             = page in readyPages,
                    interactionListener = interactionListener,
                    modifier            = Modifier.fillMaxSize(),
                )
            }
        }

        AppBar(
            title       = "Quran Reels",
            onBackClick = interactionListener::onBackClicked,
            modifier    = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp),
        )
    }
}
