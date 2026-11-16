package dev.sayed.mehrabalmomen.presentation.screen.quran.SurahAyat

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
fun SurahAyatScreen(
    navController: NavController,
    surahId: Int,
    arabicName: String,
    englishName: String,
    targetAyahId: Int? = null
) {
    Box(
        modifier = Modifier.fillMaxSize().background(Theme.color.surfaces.surface),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "قراءة السورة ($arabicName) مدعومة على أندرويد",
            style = Theme.textStyle.title.medium,
            color = Theme.color.primary.primary
        )
    }
}
