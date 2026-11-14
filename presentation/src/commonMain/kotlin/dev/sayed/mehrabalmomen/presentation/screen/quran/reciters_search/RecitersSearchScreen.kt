package dev.sayed.mehrabalmomen.presentation.screen.quran.reciters_search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import dev.sayed.mehrabalmomen.designsystem.theme.Theme

@Composable
fun RecitersSearchScreen(
    navController: NavController,
    viewModel: RecitersSearchViewModel
) {
    val state by viewModel.screenState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.color.surfaces.surface),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "البحث عن قارئ",
            style = Theme.textStyle.title.large,
            color = Theme.color.primary.primary,
            modifier = Modifier.padding(16.dp)
        )
    }
}
