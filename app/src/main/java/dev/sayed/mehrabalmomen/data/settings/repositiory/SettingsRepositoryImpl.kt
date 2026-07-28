package dev.sayed.mehrabalmomen.data.settings.repositiory

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import dev.sayed.mehrabalmomen.data.settings.SettingsKeys
import dev.sayed.mehrabalmomen.data.settings.SettingsKeys.SELECTED_MOAZEN
import dev.sayed.mehrabalmomen.domain.entity.prayer.CalculationMethod
import dev.sayed.mehrabalmomen.domain.entity.location.Location
import dev.sayed.mehrabalmomen.domain.entity.prayer.Madhab
import dev.sayed.mehrabalmomen.domain.model.AppSettings
import dev.sayed.mehrabalmomen.domain.entity.prayer.PrayerSettings
import dev.sayed.mehrabalmomen.domain.entity.widget.prayer.PrayerWidgetSettings
import dev.sayed.mehrabalmomen.domain.repository.settings.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Stores and observes application preferences through DataStore.
 *
 * @property dataStore persistent preference source used by every save and observe operation.
 */
class SettingsRepositoryImpl(
    private val dataStore: DataStore<Preferences>
) : SettingsRepository {
    override suspend fun saveMadhab(madhab: Madhab) {
        dataStore.edit { it[SettingsKeys.MADHAB] = madhab.name }
    }

    override suspend fun saveCalculationMethod(method: CalculationMethod) {
        dataStore.edit { it[SettingsKeys.CALCULATION] = method.name }
    }

    /**
     * Saves the selected location and marks location setup as complete.
     *
     * The explicit configured flag allows valid coordinates such as `0.0, 0.0` to be
     * distinguished from the default values used before a location has been selected.
     *
     * @param location coordinates and descriptive address fields to persist.
     */
    override suspend fun saveLocation(location: Location) {
        dataStore.edit {
            it[SettingsKeys.LATITUDE_KEY] = location.latitude
            it[SettingsKeys.LONGITUDE_KEY] = location.longitude
            it[SettingsKeys.COUNTRY_KEY] = location.country
            it[SettingsKeys.STATE_KEY] = location.state
            it[SettingsKeys.LOCATION_CONFIGURED] = true
        }
    }

    override suspend fun saveLanguage(language: AppSettings.Language) {
        dataStore.edit { it[SettingsKeys.LANGUAGE] = language.name }
    }

    override suspend fun saveTheme(theme: AppSettings.Theme) {
        dataStore.edit { it[SettingsKeys.THEME] = theme.name }
    }

    override suspend fun setOnboardingComplete() {
        dataStore.edit { it[SettingsKeys.ONBOARDING_COMPLETE] = true }
    }

    override fun observeLocation(): Flow<Location> =
        dataStore.data.map { prefs ->
            Location(
                latitude = prefs[SettingsKeys.LATITUDE_KEY] ?: 0.0,
                longitude = prefs[SettingsKeys.LONGITUDE_KEY] ?: 0.0,
                country = prefs[SettingsKeys.COUNTRY_KEY] ?: "Unknown",
                state = prefs[SettingsKeys.STATE_KEY] ?: "Unknown"
            )
        }

    override fun observeOnboardingComplete(): Flow<Boolean> =
        dataStore.data.map { it[SettingsKeys.ONBOARDING_COMPLETE] ?: false }

    /**
     * Observes the prayer calculation settings stored by the user.
     *
     * Invalid or missing enum values are replaced with supported defaults so a corrupted or
     * older preference cannot crash settings collection.
     *
     * @return a flow that emits updated Madhab, calculation method, and location values.
     */
    override fun observePrayerSettings(): Flow<PrayerSettings> =
        dataStore.data.map { prefs ->
            prefs.toPrayerSettings()
        }

    /**
     * Observes the complete set of settings required to calculate a prayer widget snapshot.
     *
     * @return a flow containing prayer settings, the display language, and whether the user has
     * completed location setup.
     */
    override fun observePrayerWidgetSettings(): Flow<PrayerWidgetSettings> =
        dataStore.data.map { prefs ->
            PrayerWidgetSettings(
                prayerSettings = prefs.toPrayerSettings(),
                language = prefs.toLanguage(),
                isLocationConfigured = prefs.isLocationConfigured(),
            )
        }

    override suspend fun saveQuranFontSize(size: Int) {
        dataStore.edit { prefs ->
            prefs[SettingsKeys.QURAN_FONT_SIZE] = size
        }
    }

    override fun observeQuranFontSize(): Flow<Int> =
        dataStore.data.map { prefs ->
            prefs[SettingsKeys.QURAN_FONT_SIZE] ?: 20
        }

    override suspend fun saveSelectedMoazen(fileName: String) {
        dataStore.edit { prefs ->
            prefs[SELECTED_MOAZEN] = fileName
        }
    }
    override fun observeSelectedMoazen(): Flow<String> {
        return dataStore.data
            .map { prefs ->
                prefs[SELECTED_MOAZEN] ?: DEFAULT_MOAZEN_FILE_NAME
            }
    }

    override suspend fun saveTafseer(type: String) {
        dataStore.edit {
            it[SettingsKeys.TAFSEER_TYPE] = type
        }
    }

    override fun observeTafseer(): Flow<String> {
        return dataStore.data.map {
            it[SettingsKeys.TAFSEER_TYPE] ?: "tf_ab_mokhtasar_ar.json"
        }
    }

    /**
     * Observes the application-wide settings while applying safe defaults to stored enum values.
     *
     * @return a flow that emits the current prayer, alarm, theme, and language settings.
     */
    override fun observeAppSettings(): Flow<AppSettings> =
        dataStore.data.map { prefs ->
            AppSettings(
                prayerSettings = prefs.toPrayerSettings(),
                alarmsScheduled = prefs[SettingsKeys.ALARMS_SCHEDULED] ?: false,
                theme = prefs.toTheme(),
                language = prefs.toLanguage(),
            )
        }

    /**
     * Converts stored prayer preference values into a calculation-ready settings object.
     *
     * @return prayer settings using defaults for missing or unsupported stored enum names.
     */
    private fun Preferences.toPrayerSettings(): PrayerSettings {
        return PrayerSettings(
            madhab = parseEnum(
                value = this[SettingsKeys.MADHAB],
                default = Madhab.SHAFI,
            ),
            calculationMethod = parseEnum(
                value = this[SettingsKeys.CALCULATION],
                default = CalculationMethod.EGYPTIAN,
            ),
            location = toLocation(),
        )
    }

    /**
     * Converts the stored coordinate and address preferences into a domain location.
     *
     * @return the saved location, or neutral coordinates and unknown labels when values are absent.
     */
    private fun Preferences.toLocation(): Location {
        return Location(
            latitude = this[SettingsKeys.LATITUDE_KEY] ?: 0.0,
            longitude = this[SettingsKeys.LONGITUDE_KEY] ?: 0.0,
            country = this[SettingsKeys.COUNTRY_KEY] ?: "Unknown",
            state = this[SettingsKeys.STATE_KEY] ?: "Unknown",
        )
    }

    /**
     * Determines whether the user has deliberately configured a location.
     *
     * Older installations are migrated implicitly by treating the presence of both legacy
     * coordinate keys as configured, even when their numeric values are zero.
     *
     * @return `true` when the explicit flag or both legacy coordinate keys indicate setup.
     */
    private fun Preferences.isLocationConfigured(): Boolean {
        return this[SettingsKeys.LOCATION_CONFIGURED]
            ?: (contains(SettingsKeys.LATITUDE_KEY) && contains(SettingsKeys.LONGITUDE_KEY))
    }

    /**
     * Reads the selected application theme from these preferences.
     *
     * @return the stored theme, or [AppSettings.Theme.SYSTEM] when it is missing or invalid.
     */
    private fun Preferences.toTheme(): AppSettings.Theme {
        return parseEnum(
            value = this[SettingsKeys.THEME],
            default = AppSettings.Theme.SYSTEM,
        )
    }

    /**
     * Reads the selected application language from these preferences.
     *
     * @return the stored language, or [AppSettings.Language.ARABIC] when it is missing or invalid.
     */
    private fun Preferences.toLanguage(): AppSettings.Language {
        return parseEnum(
            value = this[SettingsKeys.LANGUAGE],
            default = AppSettings.Language.ARABIC,
        )
    }

    /**
     * Parses a persisted enum name without throwing when preferences contain an obsolete value.
     *
     * @param value stored enum constant name, or `null` when no value has been saved.
     * @param default value returned when [value] is absent or does not match a constant of [T].
     * @return the matching enum constant or [default].
     */
    private inline fun <reified T : Enum<T>> parseEnum(
        value: String?,
        default: T,
    ): T {
        return value?.let { enumValue ->
            enumValues<T>().firstOrNull { it.name == enumValue }
        } ?: default
    }

    private companion object {
        const val DEFAULT_MOAZEN_FILE_NAME = "azan_makkah.mp3"
    }
}
