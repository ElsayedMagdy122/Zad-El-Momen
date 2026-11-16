package dev.sayed.mehrabalmomen.presentation.screen.quran.tasmee3

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import dev.sayed.mehrabalmomen.design_system.theme.Theme

@Composable
fun Tasmee3Screen(
    navController: NavController,
    surahId: Int,
    surahName: String,
    startAyah: Int = 1,
    endAyah: Int = -1
) {
    Box(
        modifier = Modifier.fillMaxSize().background(Theme.color.surfaces.surface),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "خاصية التسميع الصوتي مدعومة على أندرويد",
            style = Theme.textStyle.title.medium,
            color = Theme.color.primary.primary
        )
    }
}
