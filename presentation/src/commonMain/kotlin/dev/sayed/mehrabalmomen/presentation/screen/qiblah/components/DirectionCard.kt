package dev.sayed.mehrabalmomen.presentation.screen.qiblah.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.base.localizedString
import dev.sayed.mehrabalmomen.presentation.screen.qiblah.QiblahUiState
import dev.sayed.mehrabalmomen.presentation.utils.bounceClick
import org.jetbrains.compose.resources.painterResource
import zadelmomen.presentation.generated.resources.Res
import zadelmomen.presentation.generated.resources.calibrate_device
import zadelmomen.presentation.generated.resources.ic_rotate
import zadelmomen.presentation.generated.resources.qiblah_aligned
import zadelmomen.presentation.generated.resources.rotate_your_phone_to_align_with_the_qibla_direction

@Composable
fun DirectionCard(
    locationUiState: QiblahUiState.LocationUiState,
    qiblaOffset: Float,
    isAligned: Boolean,
    modifier: Modifier = Modifier,
    isLowAccuracy: Boolean = false,
    onCalibrateClick: () -> Unit = {},
) {
    Box(
        modifier = modifier.height(320.dp)
    ) {
        Column {
            DirectionInfoSection(
                locationUiState = locationUiState,
                qiblaOffset = qiblaOffset
            )
            DirectionInstruction(isAligned = isAligned)

            if (isLowAccuracy) {
                CalibrationWarning(onCalibrateClick = onCalibrateClick)
            }
        }

        LocationIndicator(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-30).dp)
        )
    }
}

@Composable
private fun ColumnScope.CalibrationWarning(onCalibrateClick: () -> Unit) {
    Row(
        modifier = Modifier
            .align(Alignment.CenterHorizontally)
            .padding(top = 12.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Theme.color.semantic.error.copy(alpha = 0.15f))
            .bounceClick(scaleDown = 0.94f) { onCalibrateClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(Res.drawable.ic_rotate),
            contentDescription = null,
            tint = Theme.color.semantic.error,
            modifier = Modifier.size(16.dp).padding(end = 4.dp)
        )
        Text(
            text = localizedString(Res.string.calibrate_device),
            color = Theme.color.semantic.error,
            style = Theme.textStyle.label.small,
            textAlign = TextAlign.Center
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
