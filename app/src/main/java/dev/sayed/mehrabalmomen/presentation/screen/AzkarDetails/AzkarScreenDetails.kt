package dev.sayed.mehrabalmomen.presentation.screen.AzkarDetails

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
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
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.component.AppBar
import dev.sayed.mehrabalmomen.design_system.component.BottomSheetDs
import dev.sayed.mehrabalmomen.design_system.component.PrimaryButton
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.domain.entity.azkar.AzkarItem
import dev.sayed.mehrabalmomen.presentation.base.localizedString
import dev.sayed.mehrabalmomen.presentation.screen.azkar.getBackgroundImageForCategory
import dev.sayed.mehrabalmomen.presentation.utils.CollectEffect
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AzkarDetailScreen(
    title: String,
    navController: NavController,
    viewModel: AzkarDetailViewModel = koinViewModel()
) {
    val state by viewModel.screenState.collectAsStateWithLifecycle()

    LaunchedEffect(title) {
        viewModel.loadAzkar(title)
    }

    CollectEffect(viewModel.effect) { effect ->
        when (effect) {
            AzkarDetailEffect.NavigateBack -> navController.popBackStack()
            is AzkarDetailEffect.ShowError -> {}
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars),
        containerColor = Theme.color.surfaces.surface,
        floatingActionButton = {
            FloatingActionButton(
                onClick = viewModel::onOpenAddDhikrBottomSheet,
                containerColor = Theme.color.primary.primary,
                contentColor = Theme.color.surfaces.surface,
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Dhikr")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            AppBar(
                title = state.title,
                onBackClick = viewModel::onClickBack
            )

            // Scrollable Grid of Items
            LazyVerticalStaggeredGrid(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                columns = StaggeredGridCells.Adaptive(320.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalItemSpacing = 16.dp,
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                // 0. Top Category Banner Image Card (Clean image without text overlay)
                item(span = StaggeredGridItemSpan.FullLine) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Theme.color.primary.primary)
                    ) {
                        Image(
                            painter = painterResource(getBackgroundImageForCategory(state.title)),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                itemsIndexed(state.items) { index, item ->
                    val remainingCount = state.itemCounts[index] ?: (item.count.toIntOrNull() ?: 1)
                    AzkarItemCard(
                        index = index + 1,
                        item = item,
                        remainingCount = remainingCount,
                        onCountClick = {
                            viewModel.onDecrementCount(index)
                        }
                    )
                }
            }
        }

        if (state.isAddDhikrBottomSheetOpen) {
            BottomSheetDs(
                onDismiss = viewModel::onDismissAddDhikrBottomSheet
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = localizedString(R.string.azkar_add_dhikr),
                        style = Theme.textStyle.title.medium,
                        color = Theme.color.primary.primary,
                        fontWeight = FontWeight.Bold
                    )

                    OutlinedTextField(
                        value = state.newContent,
                        onValueChange = viewModel::onNewContentChanged,
                        placeholder = { Text(localizedString(R.string.azkar_content_hint), style = Theme.textStyle.body.small, color = Theme.color.secondary.shadeSecondary) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = textFieldColors()
                    )

                    OutlinedTextField(
                        value = state.newCount,
                        onValueChange = viewModel::onNewCountChanged,
                        placeholder = { Text(localizedString(R.string.azkar_repeat_count_hint), style = Theme.textStyle.body.small, color = Theme.color.secondary.shadeSecondary) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = textFieldColors()
                    )

                    OutlinedTextField(
                        value = state.newDescription,
                        onValueChange = viewModel::onNewDescriptionChanged,
                        placeholder = { Text(localizedString(R.string.azkar_description_hint), style = Theme.textStyle.body.small, color = Theme.color.secondary.shadeSecondary) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = textFieldColors()
                    )

                    OutlinedTextField(
                        value = state.newReference,
                        onValueChange = viewModel::onNewReferenceChanged,
                        placeholder = { Text(localizedString(R.string.azkar_reference_hint), style = Theme.textStyle.body.small, color = Theme.color.secondary.shadeSecondary) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = textFieldColors()
                    )

                    PrimaryButton(
                        text = localizedString(R.string.azkar_save_dhikr),
                        onClick = viewModel::onSaveNewDhikr,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
fun textFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Color.Transparent,
    unfocusedBorderColor = Color.Transparent,
    focusedContainerColor = Theme.color.surfaces.surfaceHigh,
    unfocusedContainerColor = Theme.color.surfaces.surfaceHigh,
    cursorColor = Theme.color.secondary.secondary,
    focusedTextColor = Theme.color.secondary.shadeSecondary,
    unfocusedTextColor = Theme.color.secondary.shadeSecondary
)

@Composable
fun AzkarItemCard(
    index: Int,
    item: AzkarItem,
    remainingCount: Int,
    onCountClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Theme.color.surfaces.surfaceLow)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {},
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_copy_01),
                    contentDescription = "Copy",
                    tint = Theme.color.secondary.shadeSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }

            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Theme.color.surfaces.surfaceHigh),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = index.toString(),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Theme.color.primary.primary
                )
            }
        }

        Text(
            text = item.content,
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
            color = Theme.color.primary.primary,
            lineHeight = 32.sp,
            fontFamily = FontFamily(
                Font(
                    resId = R.font.hafs,
                    weight = FontWeight.Medium,
                    style = FontStyle.Normal
                )
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        val totalCount = item.count.toIntOrNull() ?: 1
        if (totalCount > 0) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (remainingCount == 0) Theme.color.primary.primary else Theme.color.surfaces.surfaceHigh)
                        .border(1.5.dp, Theme.color.secondary.secondary, RoundedCornerShape(20.dp))
                        .clickable(onClick = onCountClick)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (remainingCount == 0) localizedString(R.string.azkar_done_check) else localizedString(R.string.azkar_times_format, remainingCount),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (remainingCount == 0) Theme.color.surfaces.surface else Theme.color.secondary.secondary
                    )
                }
            }
        }

        if (item.description.isNotEmpty()) {
            Text(
                text = item.description,
                fontSize = 14.sp,
                color = Color(0xFF757575),
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )
        }

        if (item.reference.isNotEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = item.reference,
                fontSize = 12.sp,
                color = Theme.color.secondary.shadeSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}
