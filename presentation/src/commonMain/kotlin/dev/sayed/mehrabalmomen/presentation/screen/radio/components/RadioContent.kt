package dev.sayed.mehrabalmomen.presentation.screen.radio.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import dev.sayed.mehrabalmomen.design_system.component.AppBar
import dev.sayed.mehrabalmomen.design_system.component.PrimaryToast
import dev.sayed.mehrabalmomen.design_system.component.ToastDetails
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.base.localizedString
import dev.sayed.mehrabalmomen.presentation.screen.radio.RadioChannelsViewModel
import dev.sayed.mehrabalmomen.presentation.screen.radio.RadioUiState
import dev.sayed.mehrabalmomen.presentation.utils.bounceClick
import zadelmomen.presentation.generated.resources.*

@Composable
fun RadioContent(
    state: RadioUiState,
    viewModel: RadioChannelsViewModel,
    toast: ToastDetails?
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.color.surfaces.surface)
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
            AppBar(
                title = localizedString(Res.string.quran_radio),
                isBackEnabled = false,
                onBackClick = {}
            )
            FlowRow(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .padding(bottom = 16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                state.categories.forEach { category ->

                    val isSelected = category.id == state.selectedCategoryId
                    val categoryName = if (isRtl) category.nameAr else category.nameEn
                    CategoryChip(
                        text = categoryName,
                        selected = isSelected,
                        onClick = { viewModel.onCategorySelected(category.id) }
                    )
                }
            }
            RadioChannelsGrid(state, viewModel)
        }

        toast?.let {
            ToastContainer(it)
        }
    }
}

@Composable
private fun CategoryChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val targetBgColor = if (selected) Theme.color.primary.primary else Theme.color.surfaces.surfaceLow
    val targetTextColor = if (selected) Theme.color.surfaces.surfaceLow else Theme.color.primary.primary

    val backgroundColor by animateColorAsState(
        targetValue = targetBgColor,
        animationSpec = tween(durationMillis = 250),
        label = "ChipBgAnimation"
    )
    val textColor by animateColorAsState(
        targetValue = targetTextColor,
        animationSpec = tween(durationMillis = 250),
        label = "ChipTextAnimation"
    )

    val borderColor = if (selected) Color.Transparent else Theme.color.semantic.disabled

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(24.dp))
            .border(1.dp, borderColor, RoundedCornerShape(24.dp))
            .background(backgroundColor)
            .bounceClick(scaleDown = 0.94f) { onClick() }
    ) {
        Text(
            text = text,
            color = textColor,
            style = Theme.textStyle.label.medium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
        )
    }
}

@Composable
private fun BoxScope.ToastContainer(
    toast: ToastDetails
) {
    PrimaryToast(
        modifier = Modifier
            .align(Alignment.TopCenter)
            .padding(top = 24.dp),
        data = toast,
        isSuccess = false
    )
}
