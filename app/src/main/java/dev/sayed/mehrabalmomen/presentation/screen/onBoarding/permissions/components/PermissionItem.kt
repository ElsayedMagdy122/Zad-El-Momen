package dev.sayed.mehrabalmomen.presentation.screen.onBoarding.permissions.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.theme.MehrabTheme
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.base.localizedString

@Composable
fun PermissionItem(
    icon: Int,
    title: Int,
    description: Int,
    isGranted: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Theme.color.surfaces.surfaceLow)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Theme.color.surfaces.surfaceHigh),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                tint = Theme.color.primary.primary,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = localizedString(title),
                style = Theme.textStyle.title.medium,
                color = Theme.color.primary.shadePrimary
            )
            Text(
                text = localizedString(description),
                style = Theme.textStyle.body.small,
                color = Theme.color.secondary.shadeSecondary,
                textAlign = TextAlign.Start
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(if (isGranted) Color.Transparent else Theme.color.primary.primary)
                .clickable(enabled = !isGranted) { onClick() }
                .padding(horizontal = 12.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = localizedString(if (isGranted) R.string.granted else R.string.allow),
                style = Theme.textStyle.label.medium,
                color = if (isGranted) Theme.color.primary.primary else Theme.color.primary.onPrimary
            )
        }
    }
}

@Preview
@Composable
private fun PermissionItemPreview() {
    Column() {
        MehrabTheme(isDarkTheme = true) {
            PermissionItem(
                icon = R.drawable.ic_location,
                title = R.string.location_permission,
                description = R.string.location_permission_desc,
                isGranted = true,
                onClick = {}
            )
            PermissionItem(
                icon = R.drawable.ic_location,
                title = R.string.location_permission,
                description = R.string.location_permission_desc,
                isGranted = false,
                onClick = {}
            )
        }
    }
}