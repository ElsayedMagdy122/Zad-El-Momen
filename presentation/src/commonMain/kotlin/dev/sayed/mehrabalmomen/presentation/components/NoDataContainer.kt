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
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.design_system.component.PrimaryButton
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import zadelmomen.presentation.generated.resources.Res
import zadelmomen.presentation.generated.resources.no_data_yet
import zadelmomen.presentation.generated.resources.no_downloaded_reciters_for_this_surah
import zadelmomen.presentation.generated.resources.start_download_now

@Composable
fun NoDataContainer(onDownloadNow: () -> Unit, modifier: Modifier = Modifier) {
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
            painter = painterResource(Res.drawable.no_data_yet),
            contentDescription = null,
        )
        Text(
            modifier = Modifier.padding(top = 12.dp, bottom = 2.dp),
            text = stringResource(Res.string.no_downloaded_reciters_for_this_surah),
            color = Theme.color.primary.shadePrimary,
            style = Theme.textStyle.title.small
        )
        PrimaryButton(
            modifier = Modifier
                .padding(top = 12.dp)
                .widthIn(min = 320.dp, max = 400.dp),
            text = stringResource(Res.string.start_download_now),
            onClick = onDownloadNow
        )
    }
}
