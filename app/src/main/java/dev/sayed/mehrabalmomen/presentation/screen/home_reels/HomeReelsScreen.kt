package dev.sayed.mehrabalmomen.presentation.screen.home_reels

import android.content.ClipData
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.component.PrimaryToast
import dev.sayed.mehrabalmomen.design_system.component.ToastDetails
import dev.sayed.mehrabalmomen.presentation.components.LoadingContainer
import dev.sayed.mehrabalmomen.presentation.components.NoInternetContainer
import dev.sayed.mehrabalmomen.presentation.navigation.Route
import dev.sayed.mehrabalmomen.presentation.screen.home_reels.components.ReelViewCard
import dev.sayed.mehrabalmomen.presentation.utils.CollectEffect
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeReelsScreen(navController: NavController, viewModel: HomeReelsViewModel = koinViewModel()) {
    val state by viewModel.screenState.collectAsStateWithLifecycle()
    HomeReelsScreenContent(state, viewModel)
    var toast by remember { mutableStateOf<ToastDetails?>(null) }
    val context = LocalContext.current

    CollectEffect(viewModel.effect) { effect ->
        when (effect) {
            is HomeReelsEffect.ShareReel -> {
                val shared = runCatching {
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "video/mp4"
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
                    viewModel.onShareCompleted(effect.reelId, true)
            }

            is HomeReelsEffect.Error -> toast = ToastDetails(
                title = effect.titleResId,
                message = effect.messageResId,
                icon = R.drawable.ic_close_circle
            )

            is HomeReelsEffect.ShowMessage -> toast = ToastDetails(
                title = effect.titleResId,
                message = effect.messageResId,
                icon = R.drawable.ic_download_01
            )

            is HomeReelsEffect.NavigateToReelsScreen -> navController.navigate(
                Route.ReelsScreen(
                    effect.reelId
                )
            )
        }
    }

    LaunchedEffect(toast) {
        toast?.let { current ->
            delay(2000)
            if (toast == current) toast = null
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        toast?.let {
            PrimaryToast(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .zIndex(10f)
                    .padding(top = 24.dp),
                data = it,
                isSuccess = false
            )
        }
    }
}


@Composable
private fun HomeReelsScreenContent(
    state: HomeReelsUiState,
    listener: HomeReelsInteractionListener
) {
    val listState = rememberLazyListState()
    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val total = listState.layoutInfo.totalItemsCount
            lastVisible >= total - 5
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) listener.loadMoreReels()
    }
    when {
        state.isLoading -> {
            Box(modifier = Modifier.fillMaxSize()) {
                LoadingContainer(Modifier.align(Alignment.Center))
            }
        }

        state.isError -> {
            Box(modifier = Modifier.fillMaxSize()) {
                NoInternetContainer(
                    onRetryClick = { listener.loadReelsPreview() },
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }

        else ->
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(state.reelsPreviewItems, key = { it.id }) { reelPreview ->
                    ReelViewCard(
                        reelPreview = reelPreview,
                        onLikeClick = listener::onLikeClick,
                        onShareClick = listener::onShareClick,
                        onThumbnailClick = listener::onThumbnailClick,
                    )
                }
                if (state.isLoadingMore) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            LoadingContainer()
                        }
                    }
                }
            }
    }
}