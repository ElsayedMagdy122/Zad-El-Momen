package dev.sayed.mehrabalmomen.presentation.screen.azkar

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import dev.sayed.mehrabalmomen.design_system.component.BottomSheetDs
import dev.sayed.mehrabalmomen.design_system.component.PrimaryButton
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.designsystem.resources.Res
import dev.sayed.mehrabalmomen.designsystem.resources.*
import dev.sayed.mehrabalmomen.presentation.base.localizedString
import dev.sayed.mehrabalmomen.presentation.navigation.Route
import dev.sayed.mehrabalmomen.presentation.screen.azkar.component.AzkarContributionHeatmap
import dev.sayed.mehrabalmomen.presentation.utils.CollectEffect
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AzkarScreen(
    navController: NavController,
    viewModel: AzkarViewModel = koinViewModel()
) {
    val state by viewModel.screenState.collectAsStateWithLifecycle()

    CollectEffect(viewModel.effect) { effect ->
        when (effect) {
            is AzkarEffect.NavigateToDetails ->
                navController.navigate(Route.AzkarDetailScreen(effect.title))
            is AzkarEffect.ShowError -> {}
            AzkarEffect.NavigateToBack -> navController.popBackStack()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.onScreenOpened()
    }

    Scaffold(
        containerColor = Theme.color.surfaces.surface,
        floatingActionButton = {
            FloatingActionButton(
                onClick = viewModel::onOpenAddCategoryBottomSheet,
                containerColor = Theme.color.primary.primary,
                contentColor = Theme.color.primary.onPrimary,
                shape = CircleShape,
                modifier = Modifier
                    .padding(bottom = 72.dp, end = 8.dp)
                    .size(56.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Dhikr Category"
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 100.dp),
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 90.dp)
            ) {
                // 1. Top Banner Header Item
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
                            .background(Theme.color.primary.primary)
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.golden_mosque),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .align(Alignment.BottomCenter)
                        )

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomStart)
                                .padding(horizontal = 20.dp, vertical = 20.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                text = localizedString(Res.string.azkar_welcome),
                                style = Theme.textStyle.title.large,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = localizedString(Res.string.azkar_keep_tongue),
                                style = Theme.textStyle.body.medium,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }
                    }
                }

                // 2. Daily Progress & Stats Card
                item(span = { GridItemSpan(maxLineSpan) }) {
                    DailyProgressCard(
                        streak = state.streak,
                        remainingCount = state.remainingCount,
                        completedCount = state.completedCountToday,
                        percentage = state.completionPercentage,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }

                // 3. GitHub-Style Contribution Heatmap Card
                item(span = { GridItemSpan(maxLineSpan) }) {
                    AzkarContributionHeatmap(
                        historyMap = state.historyMap,
                        selectedFilter = state.selectedFilter,
                        onSelectFilter = viewModel::onSelectTimeFilter,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }

                // 4. Categories Section Header
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = localizedString(Res.string.azkar_categories),
                            style = Theme.textStyle.title.medium,
                            color = Theme.color.primary.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // 5. Grid Categories
                items(state.categories) { category ->
                    AzkarCategoryCard(
                        title = category.title,
                        itemCount = category.itemCount,
                        iconRes = category.iconRes,
                        onClick = { viewModel.onClickCategory(category.title) },
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }
            }

            if (state.isAddCategoryBottomSheetOpen) {
                BottomSheetDs(
                    onDismiss = viewModel::onDismissAddCategoryBottomSheet
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = localizedString(Res.string.azkar_add_category),
                            style = Theme.textStyle.title.medium,
                            color = Theme.color.primary.primary,
                            fontWeight = FontWeight.Bold
                        )

                        OutlinedTextField(
                            value = state.newCategoryTitle,
                            onValueChange = viewModel::onNewCategoryTitleChanged,
                            placeholder = {
                                Text(
                                    localizedString(Res.string.azkar_category_name_hint),
                                    style = Theme.textStyle.body.medium,
                                    color = Theme.color.secondary.shadeSecondary
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Theme.color.primary.primary,
                                unfocusedBorderColor = Theme.color.semantic.disabled,
                                focusedContainerColor = Theme.color.surfaces.surfaceLow,
                                unfocusedContainerColor = Theme.color.surfaces.surfaceLow
                            )
                        )

                        PrimaryButton(
                            text = localizedString(Res.string.azkar_save_category),
                            onClick = viewModel::onSaveNewCategory,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DailyProgressCard(
    streak: Int,
    remainingCount: Int,
    completedCount: Int,
    percentage: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Theme.color.surfaces.surfaceLow)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(painter = painterResource(Res.drawable.fire), contentDescription = null, tint = Color(0xFFFF9800), modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = streak.toString(), fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Theme.color.primary.primary)
                    Text(text = localizedString(Res.string.azkar_days_streak), fontSize = 11.sp, color = Theme.color.secondary.shadeSecondary)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(painter = painterResource(Res.drawable.ic_history), contentDescription = null, tint = Color(0xFF2196F3), modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = remainingCount.toString(), fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Theme.color.primary.primary)
                    Text(text = localizedString(Res.string.azkar_remaining), fontSize = 11.sp, color = Theme.color.secondary.shadeSecondary)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(painter = painterResource(Res.drawable.check), contentDescription = null, tint = Color(0xFF4CAF50), modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = completedCount.toString(), fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Theme.color.primary.primary)
                    Text(text = localizedString(Res.string.azkar_completed), fontSize = 11.sp, color = Theme.color.secondary.shadeSecondary)
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier.size(60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        progress = { (percentage / 100f).coerceIn(0f, 1f) },
                        modifier = Modifier.fillMaxSize(),
                        color = Theme.color.primary.primary,
                        trackColor = Theme.color.surfaces.surfaceHigh,
                        strokeWidth = 5.dp
                    )
                    Text(
                        text = "$percentage%",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Theme.color.primary.primary
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = localizedString(Res.string.azkar_of_daily),
                    fontSize = 10.sp,
                    color = Theme.color.secondary.shadeSecondary
                )
            }
        }
    }
}

@Composable
fun AzkarCategoryCard(
    title: String,
    itemCount: Int,
    iconRes: DrawableResource,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 145.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Theme.color.surfaces.surfaceLow)
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Theme.color.surfaces.surfaceHigh),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(iconRes),
                    contentDescription = title,
                    tint = Theme.color.primary.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
            Text(
                text = title,
                style = Theme.textStyle.body.medium.copy(
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = Theme.color.primary.primary,
                textAlign = TextAlign.Center,
                minLines = 2,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = localizedString(Res.string.azkar_dhikr_count_format, itemCount),
            style = Theme.textStyle.body.small.copy(fontSize = 11.sp),
            color = Theme.color.secondary.shadeSecondary,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}
