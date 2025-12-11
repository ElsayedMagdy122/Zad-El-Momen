package dev.sayed.mehrabalmomen.presentation.screen.qiblah.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.design_system.theme.Theme

@Composable
fun LocationIndicator(modifier: Modifier = Modifier) {
    Box(modifier = modifier.size(60.dp)) {
        CenterCircle()
        ArrowLineHorizontal(
            modifier = Modifier.align(Alignment.CenterStart)
        )
        ArrowLineHorizontal(
            modifier = Modifier.align(Alignment.CenterEnd)
        )
        ArrowLineVertical(
            modifier = Modifier.align(Alignment.TopCenter)
        )
        ArrowLineVertical(
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun BoxScope.CenterCircle() {
    Box(
        modifier = Modifier
            .size(24.dp)
            .align(Alignment.Center)
            .clip(CircleShape)
            .border(2.dp, Theme.color.primary.primary, CircleShape)
            .background(Theme.color.secondary.secondaryText)
    )
}

@Composable
private fun BoxScope.ArrowLineHorizontal(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(width = 16.dp, height = 2.dp)
            .background(Theme.color.surfaces.surfaceHigh)
    )
}

@Composable
private fun BoxScope.ArrowLineVertical(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(width = 2.dp, height = 16.dp)
            .background(Theme.color.surfaces.surfaceHigh)
    )
}

@Preview
@Composable
private fun LocationIndicatorPreview() {
    LocationIndicator()
}