package dev.sayed.mehrabalmomen.presentation.screen.maps.components

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.design_system.component.PrimaryToast
import dev.sayed.mehrabalmomen.design_system.component.ToastDetails

@Composable
fun BoxScope.MapsToast(
    data: ToastDetails,
    isSuccess: Boolean
) {
    PrimaryToast(
        data = data,
        isSuccess = isSuccess,
        modifier = Modifier
            .align(Alignment.TopCenter)
            .padding(top = 24.dp)
    )
}