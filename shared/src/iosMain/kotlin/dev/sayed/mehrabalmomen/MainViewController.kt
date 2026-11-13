package dev.sayed.mehrabalmomen

import androidx.compose.ui.window.ComposeUIViewController
import dev.sayed.mehrabalmomen.di.initKoin
import platform.UIKit.UIViewController

fun createMainViewController(): UIViewController = ComposeUIViewController {
    App()
}

// Called from Swift
fun startKoin() {
    initKoin()
}
