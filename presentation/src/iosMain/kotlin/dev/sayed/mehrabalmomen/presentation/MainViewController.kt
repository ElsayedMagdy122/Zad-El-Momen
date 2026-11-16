package dev.sayed.mehrabalmomen.presentation

import androidx.compose.ui.window.ComposeUIViewController
import dev.sayed.mehrabalmomen.presentation.navigation.MainContainer
import platform.UIKit.UIViewController

fun createMainViewController(): UIViewController = ComposeUIViewController {
    MainContainer()
}
