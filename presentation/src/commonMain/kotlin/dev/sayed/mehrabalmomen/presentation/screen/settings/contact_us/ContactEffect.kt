package dev.sayed.mehrabalmomen.presentation.screen.settings.contact_us

sealed interface ContactEffect {
    data class OpenFacebook(val link: String) : ContactEffect
    data class OpenYoutube(val link: String) : ContactEffect
    data class OpenEmail(val email: String) : ContactEffect
}