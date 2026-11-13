package dev.sayed.mehrabalmomen.presentation.screen.settings.contact_us

import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

data class ContactUsUiState(
    val items: List<ContactItemUiState> = emptyList()
) {
    data class ContactItemUiState(
        val type: ContactType,
        val title: StringResource,
        val description: StringResource,
        val icon: DrawableResource
    )
}

enum class ContactType {
    EMAIL,
    FACEBOOK,
    YOUTUBE
}
