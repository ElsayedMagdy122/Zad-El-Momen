package dev.sayed.mehrabalmomen.design_system.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun PrimaryToast(
    data: ToastDetails,
    modifier: Modifier = Modifier,
    durationMillis: Long = 3000L
) {
    var visible by remember { mutableStateOf(true) }

    LaunchedEffect(data) {
        visible = true
        delay(durationMillis)
        visible = false
    }

    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .background(
                    color = Color(0xCC323232),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(data.icon),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )

            Spacer(Modifier.size(8.dp))

            Column {
                Text(
                    text = data.title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(
                    text = data.message,
                    color = Color.White,
                    fontSize = 12.sp
                )
            }
        }
    }
}

data class ToastDetails(
    val title: String,
    val message: String,
    val icon: Int
)