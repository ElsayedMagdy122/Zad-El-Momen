package dev.sayed.mehrabalmomen.presentation.screen.settings.contact_us

import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.presentation.base.BaseViewModel

class ContactViewModel :
    BaseViewModel<ContactUsUiState, ContactEffect>(
        ContactUsUiState(
            items = listOf(
                ContactUsUiState.ContactItemUiState(
                    type = ContactType.EMAIL,
                    title = R.string.email_address,
                    description = R.string.email_value,
                    icon = R.drawable.ic_email_send
                ),
                ContactUsUiState.ContactItemUiState(
                    type = ContactType.FACEBOOK,
                    title = R.string.facebook_account,
                    description = R.string.facebook_value,
                    icon = R.drawable.ic_facebook
                ),
                ContactUsUiState.ContactItemUiState(
                    type = ContactType.YOUTUBE,
                    title = R.string.youtube,
                    description = R.string.youtube_value,
                    icon = R.drawable.ic_youtube
                )
            )
        )
    ),
    ContactInteractionListener {

    override fun onContactClick(type: ContactType) {
        when (type) {
            ContactType.EMAIL -> {
                sendEffect(
                    ContactEffect.OpenEmail(
                        "help.zadelmomen@gmail.com"
                    )
                )
            }

            ContactType.FACEBOOK -> {
                sendEffect(
                    ContactEffect.OpenFacebook(
                        "https://www.facebook.com/profile.php?id=61573336992983"
                    )
                )
            }

            ContactType.YOUTUBE -> {
                sendEffect(
                    ContactEffect.OpenYoutube(
                        "https://www.youtube.com/@ZadElmomen-x9d"
                    )
                )
            }
        }
    }
}