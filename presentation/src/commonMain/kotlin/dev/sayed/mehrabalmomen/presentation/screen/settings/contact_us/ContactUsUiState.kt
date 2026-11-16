package dev.sayed.mehrabalmomen.presentation.screen.settings.contact_us

import dev.sayed.mehrabalmomen.R

data class ContactUsUiState(
    val items: List<ContactItemUiState> = emptyList()
) {
    data class ContactItemUiState(
        val type: ContactType,
        val title: Int,
        val description: Int,
        val icon: Int
    )
}

enum class ContactType {
    EMAIL,
    FACEBOOK,
    YOUTUBE
}