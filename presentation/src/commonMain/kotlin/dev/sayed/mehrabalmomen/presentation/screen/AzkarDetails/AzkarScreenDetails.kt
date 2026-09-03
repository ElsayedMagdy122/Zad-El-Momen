package dev.sayed.mehrabalmomen.presentation.screen.AzkarDetails

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.component.AppBar
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.domain.entity.azkar.AzkarItem
import dev.sayed.mehrabalmomen.presentation.utils.CollectEffect
import org.koin.androidx.compose.koinViewModel

@Composable
fun AzkarDetailScreen(
    title: String,
    navController: NavController,
    viewModel: AzkarDetailViewModel = koinViewModel()
) {
    val state by viewModel.screenState.collectAsStateWithLifecycle()

    LaunchedEffect(title) {
        viewModel.loadAzkar(title)
    }

    CollectEffect(viewModel.effect) { effect ->
        when (effect) {
            AzkarDetailEffect.NavigateBack ->
                navController.popBackStack()

            is AzkarDetailEffect.ShowError -> {
            }
        }
    }

    LazyVerticalStaggeredGrid(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.color.surfaces.surface)
            .windowInsetsPadding(WindowInsets.systemBars)
            .padding(horizontal = 16.dp),
        columns = StaggeredGridCells.Adaptive(320.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalItemSpacing = 16.dp,
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {

        item(span = StaggeredGridItemSpan.FullLine) {
            AppBar(
                title = state.title,
                onBackClick = viewModel::onClickBack
            )
        }

        items(state.items) { item ->
            AzkarItemCard(item)
        }
    }
}

@Composable
fun AzkarItemCard(item: AzkarItem, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Theme.color.surfaces.surfaceLow)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = item.content,
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
            color = Theme.color.primary.primary,
            lineHeight = 32.sp,
            fontFamily = FontFamily(
                Font(
                    resId = R.font.hafs,
                    weight = FontWeight.Medium,
                    style = FontStyle.Normal
                )
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )


        if (item.count.toInt() > 0) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Theme.color.surfaces.surfaceHigh)
                    .border(2.dp, Theme.color.secondary.secondary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = item.count.toString(),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Theme.color.secondary.secondary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
        }

        if (item.description.isNotEmpty()) {
            Text(
                text = item.description,
                fontSize = 14.sp,
                color = Color(0xFF757575),
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )
        }

        if (item.reference.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = item.reference,
                fontSize = 12.sp,
                color = Theme.color.secondary.shadeSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview
@Composable
private fun AzkarItemCardPreview() {
    AzkarItemCard(
        item = AzkarItem(
            content = "سُبْحَانَ اللَّهِ وَبِحَمْدِه",
            count = "3",
            description = "حُطَّتْ خَطَايَاهُ وَإِنْ كَانَتْ مِثْلَ زَبَدِ الْبَحْرِ. لَمْ يَأْتِ أَحَدٌ يَوْمَ الْقِيَامَةِ بِأَفْضَلَ مِمَّا جَاءَ بِهِ إِلَّا أَحَدٌ قَالَ مِثْلَ مَا قَالَ أَوْ زَادَ عَلَيْهِ.",
            reference = ""
        )
    )
}