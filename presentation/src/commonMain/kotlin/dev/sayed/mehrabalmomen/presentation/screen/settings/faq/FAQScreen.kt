package dev.sayed.mehrabalmomen.presentation.screen.settings.faq

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dev.sayed.mehrabalmomen.designsystem.theme.Theme

@Composable
fun FAQScreen(
    navController: NavController
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.color.surfaces.surface),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "الأسئلة الشائعة",
            style = Theme.textStyle.title.large,
            color = Theme.color.primary.primary,
            modifier = Modifier.padding(16.dp)
        )
    }
}
