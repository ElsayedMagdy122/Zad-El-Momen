package dev.sayed.mehrabalmomen.presentation.screen.prayers.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.theme.Theme

@Composable
fun FullPrayerTimesAppBar(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxWidth()) {
        Icon(
            modifier = Modifier.align(Alignment.CenterStart),
            painter = painterResource(id = R.drawable.arrow_back),
            contentDescription = null,
            tint = Theme.color.primary.primary
        )
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = "Prayer Times",
            color = Theme.color.primary.primary,
            style = Theme.textStyle.title.large
        )
    }
}