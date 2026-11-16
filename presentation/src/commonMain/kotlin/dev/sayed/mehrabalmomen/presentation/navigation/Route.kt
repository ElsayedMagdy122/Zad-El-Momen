package dev.sayed.mehrabalmomen.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface Route {
    val route: String

    @Serializable
    data object AppRoute : Route {
        override val route = "app_route"
    }

    @Serializable
    data object HomeScreen : Route {
        override val route = "home_screen"
    }
}
