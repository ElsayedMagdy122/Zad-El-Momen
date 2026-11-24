package dev.sayed.mehrabalmomen.presentation.screen.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.screen.home.HomeUiState
import org.jetbrains.compose.resources.painterResource
import zadelmomen.presentation.generated.resources.Res
import zadelmomen.presentation.generated.resources.ic_notifications

@Composable
fun HomeAppBar(
    state: HomeUiState,
    modifier: Modifier = Modifier,
    onNotificationClick: () -> Unit = {},
    onLocationClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 16.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        LocationCarousel(locationUiState = state.location, onClick = onLocationClick)

        Spacer(modifier = Modifier.weight(1f))

        Box(contentAlignment = Alignment.TopEnd) {
            IconButton(onClick = onNotificationClick) {
                Icon(
                    painter = painterResource(Res.drawable.ic_notifications),
                    contentDescription = "الإشعارات",
                    tint = Theme.color.primary.primary,
                    modifier = Modifier.size(26.dp)
                )
            }

            if (state.unreadNotificationsCount > 0) {
                Box(
                    modifier = Modifier
                        .padding(top = 4.dp, end = 4.dp)
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(Theme.color.semantic.error),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (state.unreadNotificationsCount > 99) "99+" else state.unreadNotificationsCount.toString(),
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
