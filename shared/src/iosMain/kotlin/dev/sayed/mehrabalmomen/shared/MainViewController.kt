package dev.sayed.mehrabalmomen.shared

import androidx.compose.ui.window.ComposeUIViewController
import dev.sayed.mehrabalmomen.shared.di.initKoin
import platform.UIKit.UIViewController

fun createMainViewController(): UIViewController = ComposeUIViewController {
    App()
}

fun startKoin() {
    initKoin()
}
