package dev.sayed.mehrabalmomen.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.component.PrimaryButton
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.base.localizedString

@Composable
fun NoInternetContainer(
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            painter = painterResource(R.drawable.ic_no_internet),
            contentDescription = null,
        )
        Text(
            modifier = Modifier.padding(top = 12.dp, bottom = 2.dp),
            text = localizedString(R.string.oops_it_looks_like_you_re_offline),
            color = Theme.color.primary.shadePrimary,
            style = Theme.textStyle.title.small
        )
        Text(
            modifier = Modifier,
            text = localizedString(R.string.check_your_connection_and_try_again),
            color = Theme.color.secondary.shadeSecondary,
            style = Theme.textStyle.body.small
        )
        PrimaryButton(
            modifier = Modifier
                .padding(top = 12.dp)
                .widthIn(min = 320.dp, max = 400.dp),
            text = localizedString(R.string.re_try),
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