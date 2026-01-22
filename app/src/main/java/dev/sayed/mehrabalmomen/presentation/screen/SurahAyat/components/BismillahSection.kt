package dev.sayed.mehrabalmomen.presentation.screen.SurahAyat.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.R

@Composable
fun BismillahSection(color: androidx.compose.ui.graphics.Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_bismillah),
            contentDescription = null,
            modifier = Modifier
                .padding(horizontal = 74.dp),
            colorFilter = ColorFilter.tint(color)
        )
    }
}