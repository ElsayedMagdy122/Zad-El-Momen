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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.designsystem.resources.Res
import dev.sayed.mehrabalmomen.designsystem.resources.ic_empty_search
import dev.sayed.mehrabalmomen.designsystem.resources.ic_search_warning
import dev.sayed.mehrabalmomen.designsystem.resources.no_results_found_subtitle
import dev.sayed.mehrabalmomen.designsystem.resources.no_results_found_title
import dev.sayed.mehrabalmomen.designsystem.resources.seconds
import dev.sayed.mehrabalmomen.designsystem.resources.shadow
import dev.sayed.mehrabalmomen.designsystem.resources.start_searching_title
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun SearchEmptyContainer(
    isStartState: Boolean,
    isResultsState: Boolean,
    subtitle: StringResource,
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
            text = stringResource(getTitleResource(isStartState)),
            style = Theme.textStyle.title.small,
            color = Theme.color.primary.shadePrimary,
            textAlign = TextAlign.Center,
        )

        Text(
            text = stringResource(
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
            painter = painterResource(Res.drawable.shadow),
            contentDescription = null,
        )
        Image(
            modifier = modifier.padding(bottom = 12.dp),
            painter = painterResource(getIconResource(isStartState)),
            contentDescription = null
        )
    }
}

private fun getIconResource(isStartState: Boolean): DrawableResource {
    return if (isStartState) Res.drawable.ic_empty_search else Res.drawable.ic_search_warning
}

private fun getTitleResource(isStartState: Boolean): StringResource {
    return if (isStartState) Res.string.start_searching_title else Res.string.no_results_found_title
}

private fun getSubtitleResource(
    isStartState: Boolean,
    startStateSubtitle: StringResource
): StringResource {
    return if (isStartState) startStateSubtitle else Res.string.no_results_found_subtitle
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
        SearchEmptyContainer(true, false, subtitle = Res.string.seconds)
    }
}
