package dev.sayed.mehrabalmomen.presentation.screen.onBoarding.permissions.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.theme.MehrabTheme

@Composable
fun PermissionCard(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Image(
            modifier = Modifier.size(120.dp),
            painter = painterResource(id = R.drawable.bg_warning),
            contentDescription = null,
        )
    }
}

@Preview
@Composable
private fun PermissionCardPreview() {
    MehrabTheme(isDarkTheme = true) {
        PermissionCard()
    }
}
