package dev.sayed.mehrabalmomen.presentation.screen.qiblah.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.designsystem.resources.Res
import dev.sayed.mehrabalmomen.designsystem.resources.qiblah_aligned
import dev.sayed.mehrabalmomen.designsystem.resources.rotate_your_phone_to_align_with_the_qibla_direction
import dev.sayed.mehrabalmomen.presentation.base.localizedString
import dev.sayed.mehrabalmomen.presentation.screen.qiblah.QiblahUiState

@Composable
fun DirectionCard(
    locationUiState: QiblahUiState.LocationUiState,
    qiblaOffset: Float,
    isAligned: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.height(280.dp)
    ) {
        Column {
            DirectionInfoSection(
                locationUiState = locationUiState,
                qiblaOffset = qiblaOffset
            )
            DirectionInstruction(isAligned = isAligned)
        }

        LocationIndicator(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-30).dp)
        )
    }
}

@Composable
private fun ColumnScope.DirectionInstruction(isAligned: Boolean) {
    val text = if (isAligned) Res.string.qiblah_aligned else Res.string.rotate_your_phone_to_align_with_the_qibla_direction
    Text(
        modifier = Modifier
            .align(Alignment.CenterHorizontally)
            .padding(top = 16.dp),
        text = localizedString(text),
        color = if (isAligned) Theme.color.semantic.success else Theme.color.primary.primary,
        style = Theme.textStyle.label.medium,
        textAlign = TextAlign.Center
    )
}
