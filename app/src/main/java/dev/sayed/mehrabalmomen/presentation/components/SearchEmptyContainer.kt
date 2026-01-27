package dev.sayed.mehrabalmomen.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.base.localizedString

@Composable
fun SearchEmptyContainer(
    isStartState: Boolean,
    isResultsState: Boolean,
    subtitle: Int,
    modifier: Modifier = Modifier
) {
    if (!(isStartState || isResultsState)) return

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        EmptyStateIcon(
            isStartState = isStartState,
        )

        Text(
            text = localizedString(getTitleResource(isStartState)),
            style = Theme.textStyle.title.small,
            color = Theme.color.primary.shadePrimary,
            textAlign = TextAlign.Center,
        )

        Text(
            text = localizedString(
                getSubtitleResource(
                    isStartState,
                    startStateSubtitle = subtitle
                )
            ),
            style = Theme.textStyle.body.small,
            textAlign = TextAlign.Center,
            color = Theme.color.secondary.shadeSecondary,
        )
    }
}


@Composable
private fun EmptyStateIcon(
    isStartState: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center
    ) {
        Image(
            modifier = modifier.align(Alignment.BottomCenter),
            painter = painterResource(R.drawable.shadow),
            contentDescription = null,
        )
        Image(
            modifier = modifier.padding(bottom = 12.dp),
            painter = painterResource(getIconResource(isStartState)),
            contentDescription = null
        )
    }
}

private fun getIconResource(isStartState: Boolean): Int {
    return if (isStartState) R.drawable.ic_empty_search else R.drawable.ic_search_warning
}

private fun getTitleResource(isStartState: Boolean): Int {
    return if (isStartState) R.string.start_searching_title else R.string.no_results_found_title
}

private fun getSubtitleResource(
    isStartState: Boolean,
    startStateSubtitle: Int
): Int {
    return if (isStartState) startStateSubtitle else R.string.no_results_found_subtitle
}

@Preview
@Composable
private fun SearchEmptyContainerPreview() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.color.surfaces.surface),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        SearchEmptyContainer(true, false, subtitle = R.string.seconds)
    }
}




