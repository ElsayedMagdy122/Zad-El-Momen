package dev.sayed.mehrabalmomen.presentation.screen.maps.components

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.designsystem.resources.Res
import dev.sayed.mehrabalmomen.designsystem.resources.ic_detect_location
import org.jetbrains.compose.resources.painterResource

@Composable
fun BoxScope.MapsFloatingButton(onClick: () -> Unit) {
    FloatingActionButton(
        modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(16.dp),
        containerColor = Theme.color.primary.primary,
        onClick = onClick
    ) {
        Icon(
            painter = painterResource(Res.drawable.ic_detect_location),
            contentDescription = null,
            tint = Theme.color.primary.onPrimary
        )
    }
}
