package dev.sayed.mehrabalmomen.presentation.widget.prayer

import android.content.Context
import android.content.res.Configuration
import androidx.annotation.StringRes
import dev.sayed.mehrabalmomen.R
import java.util.Locale

/**
 * Converts Latin digits in this string to Arabic-Indic digits when the device locale is Arabic.
 *
 * @receiver text that may contain Latin numeric digits.
 * @param context Android context whose current resource locale controls digit localization.
 * @return the original text for non-Arabic locales, or a copy with Arabic-Indic digits.
 */
fun String.localizedDigits(context: Context): String {
    val locale = context.resources.configuration.locales[0]
    if (locale.language != "ar") return this

    return map { char ->
        when (char) {
            in '0'..'9' -> '٠' + (char - '0')
            else -> char
        }
    }.joinToString(separator = "")
}

/**
 * Reads a widget string using the language carried by the mapped widget state.
 *
 * @receiver widget state whose [PrayerWidgetUiState.languageCode] selects the resource locale.
 * @param context Android context used to create a localized resource configuration.
 * @param id string resource identifier to resolve.
 * @param args optional format arguments passed to the string resource.
 * @return localized string for the widget state's language, or the device language as fallback.
 */
internal fun PrayerWidgetUiState.localizedString(
    context: Context,
    @StringRes id: Int,
    vararg args: Any,
): String {
    val localizedContext = localizedContext(context)
    return localizedContext.getString(id, *args)
}

/**
 * Builds the accessibility description for a content-bearing prayer widget state.
 *
 * @receiver widget state containing next-prayer and countdown text.
 * @param context Android context used to load the localized description template.
 * @return a localized content description for launcher accessibility services.
 */
internal fun PrayerWidgetUiState.contentDescription(context: Context): String {
    return localizedString(
        context = context,
        id = R.string.prayer_widget_content_description,
        nextPrayerName,
        countdown,
    )
}

/**
 * Creates a context whose resources match the language stored in the widget state.
 *
 * @receiver widget state whose language code may be empty for non-content statuses.
 * @param context Android context used as the resource base.
 * @return a localized context when [PrayerWidgetUiState.languageCode] is present, or [context].
 */
private fun PrayerWidgetUiState.localizedContext(context: Context): Context {
    if (languageCode.isBlank()) return context

    val configuration = Configuration(context.resources.configuration)
    configuration.setLocale(Locale.forLanguageTag(languageCode))
    return context.createConfigurationContext(configuration)
}
