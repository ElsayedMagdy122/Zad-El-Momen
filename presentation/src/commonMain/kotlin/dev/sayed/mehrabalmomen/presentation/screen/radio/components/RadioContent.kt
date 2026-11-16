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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.design_system.component.AppBar
import dev.sayed.mehrabalmomen.design_system.component.PrimaryToast
import dev.sayed.mehrabalmomen.design_system.component.ToastDetails
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.base.localizedString
import dev.sayed.mehrabalmomen.presentation.screen.radio.RadioChannelsViewModel
import dev.sayed.mehrabalmomen.presentation.screen.radio.RadioUiState
import zadelmomen.presentation.generated.resources.Res
import zadelmomen.presentation.generated.resources.quran_radio

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
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                state.categories.forEach { category ->
                    val isSelected = category.id == state.selectedCategoryId
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isSelected) Theme.color.primary.primary
                                else Theme.color.surfaces.surfaceLow
                            )
                            .clickable {
                                viewModel.onCategorySelected(category.id)
                            }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = if (isRtl) category.nameAr else category.nameEn,
                            style = Theme.textStyle.label.medium,
                            color = if (isSelected) Theme.color.primary.onPrimary
                            else Theme.color.primary.shadePrimary
                        )
                    }
                }
            }

            RadioChannelsGrid(
                state = state,
                viewModel = viewModel
            )
        }

        if (toast != null) {
            PrimaryToast(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 16.dp),
                data = toast,
                isSuccess = false
            )
        }
    }
}
