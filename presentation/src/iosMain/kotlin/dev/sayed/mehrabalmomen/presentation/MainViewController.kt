package dev.sayed.mehrabalmomen.presentation

import androidx.compose.runtime.remember
import androidx.compose.ui.window.ComposeUIViewController
import androidx.navigation.compose.rememberNavController
import dev.sayed.mehrabalmomen.presentation.navigation.MainContainer
import platform.UIKit.UIViewController

fun createMainViewController(): UIViewController = ComposeUIViewController {
    val navController = rememberNavController()
    MainContainer(rootNavController = navController)
}
