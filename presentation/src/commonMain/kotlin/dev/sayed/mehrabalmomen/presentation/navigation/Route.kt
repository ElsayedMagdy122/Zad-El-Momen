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

    @Serializable
    data object CalculationMethodScreen : Route {
        override val route = "calculation_method_screen"
    }

    @Serializable
    data object PermissionsScreen : Route {
        override val route = "permissions_screen"
    }
}
