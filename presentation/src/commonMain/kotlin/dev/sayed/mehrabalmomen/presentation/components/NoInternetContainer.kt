package dev.sayed.mehrabalmomen.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.design_system.component.PrimaryButton
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.designsystem.resources.Res
import dev.sayed.mehrabalmomen.designsystem.resources.check_your_connection_and_try_again
import dev.sayed.mehrabalmomen.designsystem.resources.ic_no_internet
import dev.sayed.mehrabalmomen.designsystem.resources.no_internet_dark
import dev.sayed.mehrabalmomen.designsystem.resources.oops_it_looks_like_you_re_offline
import dev.sayed.mehrabalmomen.designsystem.resources.re_try
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun NoInternetContainer(
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = Theme.isDark
    val icon = if (isDark) Res.drawable.no_internet_dark else Res.drawable.ic_no_internet
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            modifier = Modifier.size(height = 100.dp, width = 120.dp),
            painter = painterResource(icon),
            contentDescription = null,
        )
        Text(
            modifier = Modifier.padding(top = 12.dp, bottom = 2.dp),
            text = stringResource(Res.string.oops_it_looks_like_you_re_offline),
            color = Theme.color.primary.shadePrimary,
            style = Theme.textStyle.title.small
        )
        Text(
            modifier = Modifier,
            text = stringResource(Res.string.check_your_connection_and_try_again),
            color = Theme.color.secondary.shadeSecondary,
            style = Theme.textStyle.body.small
        )
        PrimaryButton(
            modifier = Modifier
                .padding(top = 12.dp)
                .widthIn(min = 320.dp, max = 400.dp),
            text = stringResource(Res.string.re_try),
            onClick = onRetryClick
        )
    }
}

@Preview
@Composable
private fun NoInternetContainerPreview() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.color.surfaces.surface),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        NoInternetContainer(onRetryClick = {})
    }
}
