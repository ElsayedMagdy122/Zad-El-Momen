package dev.sayed.mehrabalmomen.presentation.screen.bookmarks

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.component.AppBar
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.base.LocalAppLocale
import dev.sayed.mehrabalmomen.presentation.base.localizedString
import dev.sayed.mehrabalmomen.presentation.base.toLocalizedDigits
import dev.sayed.mehrabalmomen.presentation.components.LoadingContainer
import dev.sayed.mehrabalmomen.presentation.components.PrimaryDialog
import dev.sayed.mehrabalmomen.presentation.navigation.Route
import org.koin.compose.viewmodel.koinActivityViewModel

@Composable
fun BookmarksListScreen(
    navController: NavController,
    viewModel: BookMarkListViewModel = koinActivityViewModel()
) {
    val state by viewModel.screenState.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        viewModel.getAllHistories()
    }
    var pendingDeleteItem by remember { mutableStateOf<BookmarkListUiState.AyahHistoryUi?>(null) }
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.color.surfaces.surface)
            .windowInsetsPadding(WindowInsets.systemBars)
            .padding(horizontal = 16.dp)
    ) {
        AppBar(title = localizedString(R.string.bookmarks), onBackClick = {
            navController.popBackStack()
        })
        Crossfade(targetState = state) {
            when {
                state.isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        LoadingContainer()
                    }
                }

                state.history.isEmpty() -> {
                    EmptyBookMarkList(
                        modifier = Modifier.fillMaxSize(),
                        onStartTilawahClick = { navController.navigate(Route.SurahListScreen) })
                }

                else -> {
                    LazyVerticalStaggeredGrid(
                        contentPadding = PaddingValues(vertical = 8.dp),
                        columns = StaggeredGridCells.Adaptive(320.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalItemSpacing = 8.dp
                    ) {
                        items(
                            items = state.history,
                            key = { "${it.surahId}_${it.ayahId}" }
                        ) { item ->

                            val dismissState = rememberSwipeToDismissBoxState(
                                confirmValueChange = {
                                    if (it == SwipeToDismissBoxValue.EndToStart) {
                                        pendingDeleteItem = item
                                        false
                                    } else false
                                }
                            )

                            SwipeToDismissBox(
                                state = dismissState,
                                enableDismissFromStartToEnd = false,
                                enableDismissFromEndToStart = true,
                                backgroundContent = {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(horizontal = 12.dp, vertical = 5.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Color(0xFFFEE4E2)),
                                        contentAlignment = Alignment.CenterEnd
                                    ) {
                                        Icon(
                                            painter = painterResource(R.drawable.ic_bookmark_off_02),
                                            contentDescription = null,
                                            tint = Theme.color.semantic.error,
                                            modifier = Modifier.padding(end = 12.dp)
                                        )
                                    }
                                }
                            ) {

                                BookMarkItem(
                                    state = item,
                                    onItemClick = {
                                        navController.navigate(
                                            Route.SurahAyatScreen(
                                                item.surahId,
                                                item.englishName,
                                                item.arabicName,
                                                targetAyahId = item.ayahId
                                            )
                                        )
                                    }
                                )
                            }

                        }
                    }
                    pendingDeleteItem?.let { selectedItem ->

                        RemoveDialog(
                            onDismiss = {
                                pendingDeleteItem = null
                            },
                            onConfirm = {
                                viewModel.deleteItem(
                                    surahId = selectedItem.surahId,
                                    ayahId = selectedItem.ayahId
                                )
                                pendingDeleteItem = null
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BookMarkItem(
    onItemClick: () -> Unit,
    state: BookmarkListUiState.AyahHistoryUi, modifier: Modifier = Modifier
) {
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val surahName = if (isRtl) state.arabicName else state.englishName
    val language = LocalAppLocale.current
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Theme.color.surfaces.surfaceLow)
            .clickable { onItemClick() }
            .padding(12.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = surahName,
                style = Theme.textStyle.label.medium,
                color = Theme.color.primary.shadePrimary
            )
            Box(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .size(3.dp)
                    .clip(CircleShape)
                    .background(Theme.color.semantic.shadeTertiary)
            )
            Text(
                text = state.ayahNumber.toString().toLocalizedDigits(language),
                style = Theme.textStyle.label.medium,
                color = Theme.color.primary.shadePrimary
            )
            Spacer(modifier = Modifier.weight(1f))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(R.drawable.ic_history),
                    tint = Theme.color.secondary.shadeSecondary,
                    contentDescription = null,
                )
                Text(
                    modifier = Modifier.padding(start = 2.dp),
                    text = state.timeAgo.toTimeAgo(),
                    style = Theme.textStyle.label.medium,
                    color = Theme.color.primary.shadePrimary
                )
            }

        }
        Text(
            modifier = Modifier.padding(start = 2.dp),
            text = state.ayahText,
            textAlign = TextAlign.Start,
            style = TextStyle(
                fontSize = 12.sp,
                fontFamily = FontFamily(Font(R.font.hafs)),
                lineHeight = 22.sp,
                textAlign = TextAlign.Justify,
                textDirection = TextDirection.Rtl,
            ),
            color = Theme.color.secondary.shadeSecondary
        )
    }
}

@Composable
fun RemoveDialog(onDismiss: () -> Unit, onConfirm: () -> Unit, modifier: Modifier = Modifier) {
    PrimaryDialog(onDismiss = onDismiss) {
        Box(
            modifier = modifier
                .padding(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Theme.color.surfaces.surface)
                    .clickable { onDismiss() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_close),
                    contentDescription = null,
                    tint = Theme.color.primary.primary
                )
            }
            Column(
                modifier = Modifier
                    .padding(top = 12.dp)
                    .align(Alignment.TopCenter),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = localizedString(R.string.remove_aya),
                    textAlign = TextAlign.Center,
                    style = Theme.textStyle.title.medium,
                    color = Theme.color.primary.shadePrimary
                )
                Text(
                    modifier = Modifier
                        .padding(top = 4.dp),
                    text = localizedString(R.string.are_you_sure_you_want_to_remove_this_aya_from_bookmarks),
                    maxLines = 2,
                    textAlign = TextAlign.Center,
                    overflow = TextOverflow.Ellipsis,
                    style = Theme.textStyle.body.small,
                    color = Theme.color.secondary.shadeSecondary
                )
                Text(
                    modifier = Modifier
                        .padding(top = 24.dp, end = 8.dp)
                        .align(Alignment.End)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            onConfirm()
                        },
                    text = localizedString(R.string.remove),
                    textAlign = TextAlign.End,
                    style = Theme.textStyle.label.medium,
                    color = Theme.color.semantic.error
                )
            }
        }
    }
}