package dev.sayed.mehrabalmomen.presentation.screen.quran.SurahAyat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.designsystem.resources.Res
import dev.sayed.mehrabalmomen.designsystem.resources.ic_mic
import dev.sayed.mehrabalmomen.designsystem.resources.ic_search
import dev.sayed.mehrabalmomen.presentation.components.AppBarAction
import dev.sayed.mehrabalmomen.presentation.components.QuranAppBar
import org.jetbrains.compose.resources.painterResource

@Composable
fun SurahAppBarSection(
    surahName: String,
    onBack: () -> Unit,
    onSearch: () -> Unit,
    onTasmee3: () -> Unit
) {
    QuranAppBar(
        modifier = Modifier
            .padding(horizontal = 12.dp)
            .fillMaxWidth()
            .background(Theme.color.surfaces.surface),
        title = surahName,
        onBackClick = onBack,
        titleColor = Theme.color.primary.shadePrimary,
        actions = listOf(
            AppBarAction(
                icon = painterResource(Res.drawable.ic_mic),
                onClick = onTasmee3
            ),
            AppBarAction(
                icon = painterResource(Res.drawable.ic_search),
                onClick = onSearch
            )
        )
    )
}
