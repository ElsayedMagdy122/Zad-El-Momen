package dev.sayed.mehrabalmomen.presentation.screen.settings.contact_us

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import dev.sayed.mehrabalmomen.design_system.component.AppBar
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.base.localizedString
import dev.sayed.mehrabalmomen.presentation.utils.CollectEffect
import org.koin.androidx.compose.koinViewModel

@Composable
fun ContactUsScreen(navController: NavController, viewModel: ContactViewModel = koinViewModel()) {
    val state by viewModel.screenState.collectAsStateWithLifecycle()
    CollectEffect(viewModel.effect) {
        when (it) {

            is ContactEffect.OpenEmail -> {

                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:${it.email}")
                }

                navController.context.startActivity(intent)
            }

            is ContactEffect.OpenFacebook -> {

                val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(it.link)
                )

                navController.context.startActivity(intent)
            }

            is ContactEffect.OpenYoutube -> {

                val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(it.link)
                )

                navController.context.startActivity(intent)
            }
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.color.surfaces.surface)
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {
        AppBar(
            title = "Contact Us",
            onBackClick = {
                navController.popBackStack()
            }
        )
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 320.dp),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(state.items) { item ->
                ContactItem(
                    title = localizedString(item.title),
                    description = localizedString(item.description),
                    icon = painterResource(item.icon),
                    onClick = {
                        viewModel.onContactClick(item.type)
                    }
                )
            }
        }
    }
}

@Composable
fun ContactItem(
    title: String,
    description: String,
    icon: Painter,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(
                onClick = onClick
            )
            .background(Theme.color.surfaces.surfaceLow)
            .padding(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Theme.color.surfaces.surfaceHigh),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = icon,
                contentDescription = null,
                tint = Theme.color.primary.primary
            )
        }

        Column(
            modifier = Modifier
                .padding(start = 8.dp)
                .weight(1f)
        ) {
            Text(
                text = title,
                style = Theme.textStyle.label.small,
                color = Theme.color.secondary.shadeSecondary
            )
            Text(
                text = description,
                style = Theme.textStyle.label.medium,
                color = Theme.color.primary.shadePrimary
            )
        }
    }
}
