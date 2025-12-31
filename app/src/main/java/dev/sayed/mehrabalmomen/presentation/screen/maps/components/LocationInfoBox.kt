package dev.sayed.mehrabalmomen.presentation.screen.maps.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.component.BottomSheetDs
import dev.sayed.mehrabalmomen.design_system.component.PrimaryButton
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.base.localizedString

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationInfoBox(
    placeName: String,
    addressLine: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    BottomSheetDs(onDismiss = onDismiss) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Theme.color.primary.primary)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(R.drawable.ic_gps),
                    contentDescription = null,
                    tint = Theme.color.primary.onPrimary
                )
            }
            Column(modifier = Modifier.padding(start = 8.dp)) {
                Text(text = placeName, fontSize = 20.sp, color = Color.Gray)
                Text(text = addressLine, fontSize = 16.sp, color = Color.Gray)
            }
        }
        Spacer(Modifier.height(16.dp))
        PrimaryButton(
            text = localizedString(R.string.confirm),
            onClick = onConfirm,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
