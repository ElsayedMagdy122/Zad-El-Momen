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
import dev.sayed.mehrabalmomen.domain.model.DonationPromptAction
import dev.sayed.mehrabalmomen.domain.entity.prayer.PrayerSettings
import dev.sayed.mehrabalmomen.domain.repository.settings.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class SettingsRepositoryImpl(
    private val dataStore: DataStore<Preferences>
) : SettingsRepository {

    companion object {
        private const val DAY_IN_MILLIS = 24L * 60L * 60L * 1000L
        private const val NEW_USER_GRACE_PERIOD_MILLIS = 3L * DAY_IN_MILLIS // 3 days grace
        private const val NEW_USER_MIN_LAUNCHES = 2                         // At least 2 launches
        private const val COOL_DOWN_NOT_NOW_MILLIS = 20L * DAY_IN_MILLIS     // 20 days if not now
        private const val COOL_DOWN_LATER_MILLIS = 4L * DAY_IN_MILLIS        // 4 days if later
    }

    override val lastDonationPromptTimeFlow: Flow<Long> = dataStore.data.map { prefs ->
        prefs[SettingsKeys.LAST_DONATION_PROMPT_TIME] ?: 0L
    }

    override val hasDonatedFlow: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[SettingsKeys.HAS_DONATED] ?: false
    }

    override suspend fun setLastDonationPromptTime(timeMs: Long) {
        dataStore.edit { prefs ->
            prefs[SettingsKeys.LAST_DONATION_PROMPT_TIME] = timeMs
        }
    }

    private suspend fun getFirstLaunchTime(): Long {
        return dataStore.data.map { prefs ->
            prefs[SettingsKeys.FIRST_LAUNCH_TIME] ?: 0L
        }.first()
    }

    private suspend fun getAppLaunchCount(): Int {
        return dataStore.data.map { prefs ->
            prefs[SettingsKeys.APP_LAUNCH_COUNT] ?: 0
        }.first()
    }

    private suspend fun getNextDonationPromptTime(): Long {
        return dataStore.data.map { prefs ->
            prefs[SettingsKeys.NEXT_DONATION_PROMPT_TIME] ?: 0L
        }.first()
    }

    private suspend fun setNextDonationPromptTime(timeMs: Long) {
        dataStore.edit { prefs ->
            prefs[SettingsKeys.NEXT_DONATION_PROMPT_TIME] = timeMs
        }
    }

    override suspend fun recordAppLaunch(currentTimeMs: Long) {
        dataStore.edit { prefs ->
            val firstLaunch = prefs[SettingsKeys.FIRST_LAUNCH_TIME] ?: 0L
            if (firstLaunch == 0L) {
                prefs[SettingsKeys.FIRST_LAUNCH_TIME] = currentTimeMs
            }
            val count = prefs[SettingsKeys.APP_LAUNCH_COUNT] ?: 0
            prefs[SettingsKeys.APP_LAUNCH_COUNT] = count + 1
        }
    }

    override suspend fun shouldShowDonationPrompt(currentTimeMs: Long): Boolean {
        // 1. If user already donated, never prompt automatically
        val hasDonated = hasDonatedFlow.first()
        if (hasDonated) return false

        val firstLaunchTime = getFirstLaunchTime()
        val launchCount = getAppLaunchCount()

        // 2. New user protection (Grace period: at least 3 days + at least 2 launches)
        if (firstLaunchTime == 0L || (currentTimeMs - firstLaunchTime) < NEW_USER_GRACE_PERIOD_MILLIS || launchCount < NEW_USER_MIN_LAUNCHES) {
            return false
        }

        // 3. Check next prompt schedule
        val nextPromptTime = getNextDonationPromptTime()
        if (nextPromptTime == 0L) {
            val lastTime = lastDonationPromptTimeFlow.first()
            if (lastTime == 0L) {
                return true
            } else {
                return (currentTimeMs - lastTime) >= COOL_DOWN_LATER_MILLIS
            }
        }

        return currentTimeMs >= nextPromptTime
    }

    override suspend fun onDonationPromptAction(action: DonationPromptAction, currentTimeMs: Long) {
        setLastDonationPromptTime(currentTimeMs)
        when (action) {
            DonationPromptAction.DONATED -> {
                setHasDonated(true)
                setNextDonationPromptTime(Long.MAX_VALUE)
            }
            DonationPromptAction.NOT_NOW -> {
                val randomDays = (10..15).random()
                setNextDonationPromptTime(currentTimeMs + (randomDays * DAY_IN_MILLIS))
            }
            DonationPromptAction.LATER -> {
                setNextDonationPromptTime(currentTimeMs + COOL_DOWN_LATER_MILLIS)
            }
            DonationPromptAction.DISMISSED -> {
                val randomDays = (2..4).random()
                setNextDonationPromptTime(currentTimeMs + (randomDays * DAY_IN_MILLIS))
            }
        }
    }

    override suspend fun setHasDonated(hasDonated: Boolean) {
        dataStore.edit { prefs ->
            prefs[SettingsKeys.HAS_DONATED] = hasDonated
        }
    }
    override suspend fun saveMadhab(madhab: Madhab) {
        dataStore.edit { it[SettingsKeys.MADHAB] = madhab.name }
    }

    override suspend fun saveCalculationMethod(method: CalculationMethod) {
        dataStore.edit { it[SettingsKeys.CALCULATION] = method.name }
    }

    override suspend fun saveLocation(location: Location) {
        dataStore.edit {
            it[SettingsKeys.LATITUDE_KEY] = location.latitude
            it[SettingsKeys.LONGITUDE_KEY] = location.longitude
            it[SettingsKeys.COUNTRY_KEY] = location.country
            it[SettingsKeys.STATE_KEY] = location.state
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

    override fun observePrayerSettings(): Flow<PrayerSettings> =
        dataStore.data.map { prefs ->
            PrayerSettings(
                madhab = Madhab.valueOf(
                    prefs[SettingsKeys.MADHAB] ?: Madhab.SHAFI.name
                ),
                calculationMethod = CalculationMethod.valueOf(
                    prefs[SettingsKeys.CALCULATION]
                        ?: CalculationMethod.EGYPTIAN.name
                ),
                location = Location(
                    latitude = prefs[SettingsKeys.LATITUDE_KEY] ?: 0.0,
                    longitude = prefs[SettingsKeys.LONGITUDE_KEY] ?: 0.0,
                    country = prefs[SettingsKeys.COUNTRY_KEY] ?: "Unknown",
                    state = prefs[SettingsKeys.STATE_KEY] ?: "Unknown"
                )
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
                prefs[SELECTED_MOAZEN] ?: SettingsUiState.Moazen.AZAN_MAKKAH.fileName
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

    override fun observeAppSettings(): Flow<AppSettings> =
        dataStore.data.map { prefs ->
            AppSettings(
                prayerSettings = PrayerSettings(
                    madhab = Madhab.valueOf(
                        prefs[SettingsKeys.MADHAB] ?: Madhab.SHAFI.name
                    ),
                    calculationMethod = CalculationMethod.valueOf(
                        prefs[SettingsKeys.CALCULATION]
                            ?: CalculationMethod.MUSLIM_WORLD_LEAGUE.name
                    ),
                    location = Location(
                        latitude = prefs[SettingsKeys.LATITUDE_KEY] ?: 0.0,
                        longitude = prefs[SettingsKeys.LONGITUDE_KEY] ?: 0.0,
                        country = prefs[SettingsKeys.COUNTRY_KEY] ?: "Unknown",
                        state = prefs[SettingsKeys.STATE_KEY] ?: "Unknown"
                    )
                ),

                alarmsScheduled = prefs[SettingsKeys.ALARMS_SCHEDULED] ?: false,
                theme = AppSettings.Theme.valueOf(
                    prefs[SettingsKeys.THEME] ?: AppSettings.Theme.SYSTEM.name
                ),
                language = AppSettings.Language.valueOf(
                    prefs[SettingsKeys.LANGUAGE] ?: AppSettings.Language.ARABIC.name
                )
            )
        }
}