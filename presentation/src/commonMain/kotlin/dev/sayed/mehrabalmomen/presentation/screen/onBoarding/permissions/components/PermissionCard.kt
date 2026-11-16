package dev.sayed.mehrabalmomen.presentation.screen.onBoarding.permissions.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import zadelmomen.presentation.generated.resources.Res
import zadelmomen.presentation.generated.resources.bg_warning

@Composable
fun PermissionCard(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Image(
            modifier = Modifier.size(120.dp),
            painter = painterResource(Res.drawable.bg_warning),
            contentDescription = null,
        )
    }
}
