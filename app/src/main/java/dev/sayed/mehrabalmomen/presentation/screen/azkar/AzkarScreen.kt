package dev.sayed.mehrabalmomen.presentation.screen.azkar

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.component.BottomSheetDs
import dev.sayed.mehrabalmomen.design_system.component.PrimaryButton
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.base.localizedString
import dev.sayed.mehrabalmomen.presentation.navigation.Route
import dev.sayed.mehrabalmomen.presentation.screen.azkar.component.AzkarContributionHeatmap
import dev.sayed.mehrabalmomen.presentation.utils.CollectEffect
import org.koin.androidx.compose.koinViewModel

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
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars),
        containerColor = Theme.color.surfaces.surface,
        floatingActionButton = {
            FloatingActionButton(
                onClick = viewModel::onOpenAddCategoryBottomSheet,
                containerColor = Theme.color.primary.primary,
                contentColor = Theme.color.surfaces.surface,
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Category")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Scrollable Content Grid & Cards
            LazyVerticalGrid(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
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
                            painter = painterResource(R.drawable.golden_mosque),
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
                                text = localizedString(R.string.azkar_welcome),
                                style = Theme.textStyle.title.large,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = localizedString(R.string.azkar_keep_tongue),
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
                        percentage = state.completionPercentage
                    )
                }

                // 3. Categories Header (No Icon)
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = localizedString(R.string.azkar_categories),
                            style = Theme.textStyle.title.medium,
                            color = Theme.color.primary.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // 4. Category Items Grid (3 columns)
                items(state.categories) { category ->
                    AzkarCategoryCard(
                        title = category.title,
                        itemCount = category.itemCount,
                        iconRes = category.iconRes,
                        onClick = { viewModel.onClickCategory(category.title) }
                    )
                }

                // 5. Statistics Header
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp, bottom = 8.dp),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = localizedString(R.string.azkar_your_statistics),
                            style = Theme.textStyle.title.medium,
                            color = Theme.color.primary.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // 6. GitHub-Style Contribution Heatmap
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Spacer(modifier = Modifier.height(4.dp))
                    AzkarContributionHeatmap(
                        historyMap = state.historyMap,
                        selectedFilter = state.selectedFilter,
                        onSelectFilter = viewModel::onSelectTimeFilter
                    )
                }
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
                        text = localizedString(R.string.azkar_add_category),
                        style = Theme.textStyle.title.medium,
                        color = Theme.color.primary.primary,
                        fontWeight = FontWeight.Bold
                    )

                    OutlinedTextField(
                        value = state.newCategoryTitle,
                        onValueChange = viewModel::onNewCategoryTitleChanged,
                        placeholder = {
                            Text(
                                localizedString(R.string.azkar_category_name_hint),
                                style = Theme.textStyle.body.small,
                                color = Theme.color.secondary.shadeSecondary
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedContainerColor = Theme.color.surfaces.surfaceHigh,
                            unfocusedContainerColor = Theme.color.surfaces.surfaceHigh,
                            cursorColor = Theme.color.secondary.secondary,
                            focusedTextColor = Theme.color.secondary.shadeSecondary,
                            unfocusedTextColor = Theme.color.secondary.shadeSecondary
                        )
                    )

                    PrimaryButton(
                        text = localizedString(R.string.azkar_save_category),
                        onClick = viewModel::onSaveNewCategory,
                        modifier = Modifier.fillMaxWidth()
                    )
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
                    Icon(painter = painterResource(R.drawable.fire), contentDescription = null, tint = Color(0xFFFF9800), modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = streak.toString(), fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Theme.color.primary.primary)
                    Text(text = localizedString(R.string.azkar_days_streak), fontSize = 11.sp, color = Theme.color.secondary.shadeSecondary)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(painter = painterResource(R.drawable.ic_history), contentDescription = null, tint = Color(0xFF2196F3), modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = remainingCount.toString(), fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Theme.color.primary.primary)
                    Text(text = localizedString(R.string.azkar_remaining), fontSize = 11.sp, color = Theme.color.secondary.shadeSecondary)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(painter = painterResource(R.drawable.check), contentDescription = null, tint = Color(0xFF4CAF50), modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = completedCount.toString(), fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Theme.color.primary.primary)
                    Text(text = localizedString(R.string.azkar_completed), fontSize = 11.sp, color = Theme.color.secondary.shadeSecondary)
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
                    text = localizedString(R.string.azkar_of_daily),
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
    iconRes: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Theme.color.surfaces.surfaceLow)
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
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
            style = Theme.textStyle.title.small,
            color = Theme.color.primary.primary,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
        Text(
            text = localizedString(R.string.azkar_dhikr_count_format, itemCount),
            style = Theme.textStyle.body.small,
            color = Theme.color.secondary.shadeSecondary,
            textAlign = TextAlign.Center
        )
    }
}
