package dev.sayed.mehrabalmomen.presentation.screen.onBoarding.permissions.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.designsystem.resources.Res
import dev.sayed.mehrabalmomen.designsystem.resources.set_up_permissions
import dev.sayed.mehrabalmomen.designsystem.resources.set_up_permissions_description
import dev.sayed.mehrabalmomen.presentation.base.localizedString

@Composable
fun PermissionHeader(modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = localizedString(Res.string.set_up_permissions),
            color = Theme.color.primary.shadePrimary,
            style = Theme.textStyle.title.small
        )
        Text(
            modifier = Modifier.padding(top = 2.dp),
            text = localizedString(Res.string.set_up_permissions_description),
            color = Theme.color.secondary.shadeSecondary,
            style = Theme.textStyle.body.small,
            textAlign = TextAlign.Center
        )
    }
}
