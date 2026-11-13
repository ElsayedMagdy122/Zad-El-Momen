package dev.sayed.mehrabalmomen.presentation.screen.home.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.designsystem.resources.Res
import dev.sayed.mehrabalmomen.designsystem.resources.*
import dev.sayed.mehrabalmomen.presentation.base.localizedString
import dev.sayed.mehrabalmomen.presentation.screen.home.HomeInteractionListener
import org.jetbrains.compose.resources.painterResource

@Composable
fun FeaturesSection(
    homeInteractionListener: HomeInteractionListener,
    modifier: Modifier = Modifier
) {
    val faithFeatureCards = faithFeatureCards(homeInteractionListener = homeInteractionListener)
    LazyVerticalGrid(
        modifier = modifier
            .padding(top = 12.dp)
            .heightIn(max = 1000.dp),
        columns = GridCells.Adaptive(minSize = 150.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(
            bottom = 8.dp,
            start = 16.dp,
            end = 16.dp
        )
    ) {
        items(faithFeatureCards) {
            FeatureCard(
                icon = it.icon,
                title = it.title,
                onClick = it.onClick
            )
        }
    }
}

data class FeatureItem(
    val title: String,
    val icon: Painter,
    val onClick: () -> Unit
)

@Composable
private fun faithFeatureCards(homeInteractionListener: HomeInteractionListener): List<FeatureItem> {
    return listOf(
        FeatureItem(
            title = localizedString(Res.string.quran),
            icon = painterResource(Res.drawable.ic_quran_section),
            onClick = homeInteractionListener::onClickQuran
        ),
        FeatureItem(
            title = localizedString(Res.string.qiblah_direction),
            icon = painterResource(Res.drawable.kaaba_01),
            onClick = homeInteractionListener::onClickQiblaDirection
        )
    )
}
