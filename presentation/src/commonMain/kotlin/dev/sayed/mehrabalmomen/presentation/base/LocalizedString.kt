package dev.sayed.mehrabalmomen.presentation.base

import androidx.compose.runtime.Composable
import dev.sayed.mehrabalmomen.domain.model.AppSettings
import dev.sayed.mehrabalmomen.presentation.utils.localizeAmPm as utilsLocalizeAmPm
import dev.sayed.mehrabalmomen.presentation.utils.toLocalizedDigits as utilsToLocalizedDigits
import org.jetbrains.compose.resources.PluralStringResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun localizedString(resource: StringResource, vararg formatArgs: Any): String {
    return stringResource(resource, *formatArgs)
}

@Composable
fun localizedPlural(resource: PluralStringResource, quantity: Int, vararg formatArgs: Any): String {
    return pluralStringResource(resource, quantity, *formatArgs)
}

fun String.toLocalizedDigits(language: AppSettings.Language): String =
    this.utilsToLocalizedDigits(language)

fun Int.toLocalizedDigits(language: AppSettings.Language): String =
    this.toString().utilsToLocalizedDigits(language)

fun String.localizeAmPm(language: AppSettings.Language): String =
    this.utilsLocalizeAmPm(language)
