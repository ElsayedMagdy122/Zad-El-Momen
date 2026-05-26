package dev.sayed.mehrabalmomen.presentation.screen.settings.privacy

import android.annotation.SuppressLint
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.color.darkThemeColors
import dev.sayed.mehrabalmomen.design_system.color.lightThemeColors
import dev.sayed.mehrabalmomen.design_system.component.AppBar
import dev.sayed.mehrabalmomen.design_system.theme.Theme

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun PrivacyAnPolicyScreen(
    navController: NavController,
    isDarkTheme: Boolean = false
) {

    val colors = if (isDarkTheme) darkThemeColors else lightThemeColors
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.color.surfaces.surface)
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {
        AppBar(
            title = "Privacy and policy",
            onBackClick = {
                navController.popBackStack()
            }
        )
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(R.drawable.app_icon),
                    contentDescription = null,
                    modifier = Modifier.size(64.dp)
                )

                Text(
                    text = "Last update: 26/05/2026",
                    style = Theme.textStyle.label.small,
                    color = colors.semantic.shadeTertiary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(top = 12.dp, bottom = 16.dp)
                        .fillMaxWidth()
                )
            }
        }

        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            factory = { context ->
                WebView(context).apply {
                    webViewClient = WebViewClient()

                    setBackgroundColor(android.graphics.Color.TRANSPARENT)

                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    settings.loadWithOverviewMode = true
                    settings.useWideViewPort = true

                    loadUrl("file:///android_asset/privacy.html")

                    post {
                        applyTheme(colors)
                    }
                }
            },
            update = { webView ->
                webView.applyTheme(colors)
            }
        )
    }
}

private fun WebView.applyTheme(
    colors: dev.sayed.mehrabalmomen.design_system.color.MehrabColors
) {
    val bg = colors.surfaces.surface.toHex()
    val titleColor = colors.secondary.shadeSecondary.toHex()
    val textColor = colors.semantic.shadeTertiary.toHex()
    val primary = colors.brand.brand.toHex()

    evaluateJavascript(
        """
        document.documentElement.style.setProperty('--bg', '$bg');
        document.documentElement.style.setProperty('--title', '$titleColor');
        document.documentElement.style.setProperty('--text', '$textColor');
        document.documentElement.style.setProperty('--primary', '$primary');
        """.trimIndent(),
        null
    )
}

fun Color.toHex(): String {
    return String.format(
        "#%06X",
        0xFFFFFF and this.toArgb()
    )
}