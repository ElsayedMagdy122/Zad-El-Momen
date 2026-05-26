package dev.sayed.mehrabalmomen.presentation.screen.settings.privacy

import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.color.darkThemeColors
import dev.sayed.mehrabalmomen.design_system.color.lightThemeColors
import dev.sayed.mehrabalmomen.design_system.component.AppBar
import dev.sayed.mehrabalmomen.design_system.theme.Theme

@Composable
fun PrivacyAnPolicyScreen(
    navController: NavController,
    isDarkTheme: Boolean = false
) {
    val colors = if (isDarkTheme) darkThemeColors else lightThemeColors
    val bodyTextColor = colors.semantic.shadeTertiary
    val titleTextColor = Theme.color.primary.primary

    val privacyContentList = getShortPrivacyContent()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.color.surfaces.surface)
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {
        AppBar(
            title = stringResource(R.string.privacy_and_policy),
            onBackClick = { navController.popBackStack() }
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp)
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(
                            painter = painterResource(R.drawable.app_icon),
                            contentDescription = null,
                            modifier = Modifier.size(64.dp)
                        )

                        Text(
                            text = stringResource(id = R.string.last_updated, "26 May 2026"),
                            style = Theme.textStyle.label.small,
                            color = bodyTextColor,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 12.dp, bottom = 16.dp)
                        )
                    }
                }
            }

            items(privacyContentList) { item ->
                when (item) {
                    is PrivacyModel.MainHeader -> {
                        Text(
                            text = stringResource(id = item.resId),
                            style = Theme.textStyle.title.small,
                            color = titleTextColor,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 12.dp, bottom = 8.dp)
                        )
                    }
                    is PrivacyModel.Paragraph -> {
                        Text(
                            text = stringResource(id = item.resId),
                            style = Theme.textStyle.body.medium,
                            color = bodyTextColor,
                        )
                    }
                    is PrivacyModel.BulletPoint -> {
                        Row(
                            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(
                                text = "• ",
                                style = Theme.textStyle.body.medium,
                                color = bodyTextColor
                            )
                            Text(
                                text = stringResource(id = item.resId),
                                style = Theme.textStyle.body.medium,
                                color = bodyTextColor
                            )
                        }
                    }
                    else -> {}
                }
            }
        }
    }
}
sealed interface PrivacyModel {
    data class MainHeader(@param:StringRes val resId: Int) : PrivacyModel
    data class Paragraph(@param:StringRes val resId: Int) : PrivacyModel
    data class BulletPoint(@param:StringRes val resId: Int) : PrivacyModel
}
private fun getShortPrivacyContent(): List<PrivacyModel> {
    return listOf(
        PrivacyModel.MainHeader(R.string.privacy_short_h1_collect),
        PrivacyModel.BulletPoint(R.string.privacy_short_b1_loc),
        PrivacyModel.BulletPoint(R.string.privacy_short_b2_settings),
        PrivacyModel.BulletPoint(R.string.privacy_short_b3_bugs),
        PrivacyModel.BulletPoint(R.string.privacy_short_b4_purchases),

        PrivacyModel.MainHeader(R.string.privacy_short_h1_not_collect),
        PrivacyModel.Paragraph(R.string.privacy_short_not_collect_desc),

        PrivacyModel.MainHeader(R.string.privacy_short_h1_sharing),
        PrivacyModel.BulletPoint(R.string.privacy_short_b1_sharing),
        PrivacyModel.BulletPoint(R.string.privacy_short_b2_sharing),

        PrivacyModel.MainHeader(R.string.privacy_short_h1_rights),
        PrivacyModel.BulletPoint(R.string.privacy_short_b1_rights),
        PrivacyModel.BulletPoint(R.string.privacy_short_b2_rights),

        PrivacyModel.MainHeader(R.string.privacy_short_h1_contact),
        PrivacyModel.Paragraph(R.string.privacy_short_contact_desc),
        PrivacyModel.Paragraph(R.string.privacy_short_contact_email)
    )
}