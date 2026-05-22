package dev.sayed.mehrabalmomen.presentation.screen.reels

import android.content.ClipData
import android.content.Intent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.util.lerp
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.screen.reels.components.ReelItemCard
import dev.sayed.mehrabalmomen.presentation.utils.CollectEffect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.absoluteValue

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

                            clipData = ClipData.newUri(
                                context.contentResolver,
                                "shared_video",
                                effect.cachedReelUrl.toUri()
                            )
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

    LaunchedEffect(urls.size) {
        if (urls.isNotEmpty()) {
            pool.onPageChanged(
                pagerState.currentPage,
                urls
            )
        }
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

            state.reels.isNotEmpty() ->
                VerticalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize(),
                    beyondViewportPageCount = 2,
                ) { page ->

                    val pageOffset = (
                            (pagerState.currentPage - page) + pagerState
                                .currentPageOffsetFraction
                            ).absoluteValue

                    val scale by animateFloatAsState(
                        targetValue = lerp(
                            start = 0.92f,
                            stop = 1f,
                            fraction = 1f - pageOffset.coerceIn(0f, 1f)
                        ),
                        label = "scale"
                    )

                    val alpha by animateFloatAsState(
                        targetValue = lerp(
                            start = 0.5f,
                            stop = 1f,
                            fraction = 1f - pageOffset.coerceIn(0f, 1f)
                        ),
                        label = "alpha"
                    )

                    CompositionLocalProvider(
                        LocalLayoutDirection provides LayoutDirection.Rtl
                    ) {
                        ReelItemCard(
                            item = state.reels[page],
                            player = pool.playerForPage(page, pagerState.currentPage),
                            isActive = page == pagerState.currentPage,
                            isReady = page in readyPages,
                            interactionListener = interactionListener,
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer {
                                    scaleX = scale
                                    scaleY = scale
                                    this.alpha = alpha
                                },
                        )
                    }
                }
        }
    }
}
