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
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.design_system.theme.Theme

@Composable
fun DirectionCard(direction: Float, modifier: Modifier = Modifier) {
    Box(
        modifier = Modifier.height(280.dp)
    ) {
        Column {
            DirectionInfoSection(direction)
            DirectionInstruction()
        }

        LocationIndicator(
            modifier = modifier
                .align(Alignment.TopCenter)
                .offset(y = (-30).dp)
        )
    }
}

@Composable
private fun ColumnScope.DirectionInstruction() {
    Text(
        modifier = Modifier
            .align(Alignment.CenterHorizontally)
            .padding(top = 16.dp),
        text = "Rotate your phone to align with the Qibla direction.",
        color = Theme.color.primary.primary,
        style = Theme.textStyle.label.medium,
    )
}