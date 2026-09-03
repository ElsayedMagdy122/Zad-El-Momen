package dev.sayed.mehrabalmomen.presentation.screen.bookmarks

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.component.PrimaryButton
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.base.localizedString

@Composable
fun EmptyBookMarkList(
    onStartTilawahClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(R.drawable.ic_not_saved_book_mark),
            contentDescription = null,
        )
        Text(
            modifier = Modifier.padding(top = 12.dp, bottom = 2.dp),
            text = localizedString(R.string.no_ayah_saved_yet),
            color = Theme.color.primary.shadePrimary,
            textAlign = TextAlign.Center,
            style = Theme.textStyle.title.small
        )
        Text(
            modifier = Modifier,
            textAlign = TextAlign.Center,
            text = localizedString(R.string.start_your_tilawah_and_bookmark_the_ayah_you_want_to_return_to_later),
            color = Theme.color.secondary.shadeSecondary,
            style = Theme.textStyle.body.small
        )
        PrimaryButton(
            modifier = Modifier
                .padding(top = 12.dp)
                .widthIn(min = 320.dp, max = 400.dp),
            text = localizedString(R.string.start_tilawah),
            onClick = onStartTilawahClick
        )
    }
}

@Preview
@Composable
private fun EmptyBookMarkListPreview() {
    EmptyBookMarkList(onStartTilawahClick = {})
}