package dev.sayed.mehrabalmomen.presentation.screen.settings.contact_us

import dev.sayed.mehrabalmomen.designsystem.resources.Res
import dev.sayed.mehrabalmomen.designsystem.resources.email_address
import dev.sayed.mehrabalmomen.designsystem.resources.email_value
import dev.sayed.mehrabalmomen.designsystem.resources.facebook_account
import dev.sayed.mehrabalmomen.designsystem.resources.facebook_value
import dev.sayed.mehrabalmomen.designsystem.resources.ic_email_send
import dev.sayed.mehrabalmomen.designsystem.resources.ic_facebook
import dev.sayed.mehrabalmomen.designsystem.resources.ic_youtube
import dev.sayed.mehrabalmomen.designsystem.resources.youtube
import dev.sayed.mehrabalmomen.designsystem.resources.youtube_value
import dev.sayed.mehrabalmomen.presentation.base.BaseViewModel

class ContactViewModel :
    BaseViewModel<ContactUsUiState, ContactEffect>(
        ContactUsUiState(
            items = listOf(
                ContactUsUiState.ContactItemUiState(
                    type = ContactType.EMAIL,
                    title = Res.string.email_address,
                    description = Res.string.email_value,
                    icon = Res.drawable.ic_email_send
                ),
                ContactUsUiState.ContactItemUiState(
                    type = ContactType.FACEBOOK,
                    title = Res.string.facebook_account,
                    description = Res.string.facebook_value,
                    icon = Res.drawable.ic_facebook
                ),
                ContactUsUiState.ContactItemUiState(
                    type = ContactType.YOUTUBE,
                    title = Res.string.youtube,
                    description = Res.string.youtube_value,
                    icon = Res.drawable.ic_youtube
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
