@file:OptIn(ExperimentalTime::class)

package dev.sayed.mehrabalmomen.presentation.screen.home

import androidx.lifecycle.viewModelScope
import dev.sayed.mehrabalmomen.domain.entity.location.Location
import dev.sayed.mehrabalmomen.domain.repository.ReadingProgressRepository
import dev.sayed.mehrabalmomen.domain.repository.PrayerRepository
import dev.sayed.mehrabalmomen.domain.repository.QuranRepository
import dev.sayed.mehrabalmomen.domain.repository.SettingsRepository
import dev.sayed.mehrabalmomen.presentation.base.BaseViewModel
import dev.sayed.mehrabalmomen.presentation.utils.convertMillisToHMS
import dev.sayed.mehrabalmomen.presentation.utils.getTimeDifference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class HomeViewModel(
    private val prayerRepository: PrayerRepository,
    private val readingProgressRepository: ReadingProgressRepository,
    private val settingsRepository: SettingsRepository,
    private val quranRepository: QuranRepository
) : BaseViewModel<HomeUiState, HomeEffect>(HomeUiState()), HomeInteractionListener {
    private var countdownJob: Job? = null
    private val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    private val _countdownTime = MutableStateFlow(HomeUiState.TimeUiState("00", "00", "00"))
    val countdownTime: StateFlow<HomeUiState.TimeUiState> = _countdownTime.asStateFlow()

    init {
        observeLocationChanges()
        observeContinueTilawah()
    }

    private fun observeLocationChanges() {
        viewModelScope.launch(Dispatchers.IO) {
            settingsRepository.observePrayerSettings().collect { prayerSettings ->
                updateLocationUi(prayerSettings.location)
                refreshPrayersForLocation(prayerSettings.location)
            }
        }
    }

    private fun observeContinueTilawah() {
        viewModelScope.launch(Dispatchers.IO) {
            readingProgressRepository.observe().collect { data ->
                if (data == null) return@collect

                val surah = quranRepository
                    .getSurahs()
                    .firstOrNull { it.surahNumber == data.surahId }
                    ?: return@collect

                updateState {
                    it.copy(
                        lastTilawahUi = HomeUiState.ContinueTilawahUi(
                            surahId = data.surahId,
                            nameArabic = surah.nameArabic,
                            nameEnglish = surah.nameEnglish,
                            ayahId = data.ayahId
                        )
                    )
                }
            }
        }
    }

    private fun updateLocationUi(location: Location) {
        updateState {
            it.copy(
                location = HomeUiState.LocationUiState(
                    country = location.country,
                    city = location.state
                )
            )
        }
    }

    private fun refreshPrayersForLocation(location: Location) {
        tryToCall(
            block = {
                val settings = settingsRepository
                    .observeAppSettings()
                    .first()
                    .prayerSettings

                val prayers = prayerRepository.getDailyPrayers(
                    madhab = settings.madhab,
                    calculationMethod = settings.calculationMethod,
                    location = location,
                    date = today
                )

                val zone = TimeZone.currentSystemDefault()
                prayers.map { it.toPrayerUiState(zone) }
            },
            onSuccess = { prayerList ->
                updateState {
                    it.copy(prayers = prayerList)
                }
                getNextPrayer()
            },
            onError = {}
        )
    }

    private fun getLocation() {
        tryToCall(
            block = {
                val location = settingsRepository.observeLocation().first()
                location
            },
            onSuccess = {
                val location = HomeUiState.LocationUiState(
                    country = it.country,
                    city = it.state
                )
                updateState { state ->
                    state.copy(
                        location = location
                    )
                }
            },
            onError = {
                val location = HomeUiState.LocationUiState(country = "Unknown", city = "Unknown")
                updateState { it.copy(location = location) }
            }
        )
    }

    private fun getDailyPrayers() {
        tryToCall(
            block = {
                val settings = settingsRepository.observeAppSettings().first().prayerSettings
                val prayers = prayerRepository.getDailyPrayers(
                    madhab = settings.madhab,
                    calculationMethod = settings.calculationMethod,
                    location = Location(
                        longitude = settings.location.longitude,
                        latitude = settings.location.latitude,
                        country = "",
                        state = ""
                    ),
                    date = today
                )
                val zone = TimeZone.currentSystemDefault()
                prayers.map { it.toPrayerUiState(zone = zone) }
            },
            onSuccess = { prayerList ->
                updateState { currentState ->
                    currentState.copy(prayers = prayerList)
                }
                getNextPrayer()
                getLocation()
            },
            onError = {}
        )


    }

    private fun getNextPrayer() {
        tryToCall(
            block = {
                val settings = settingsRepository.observeAppSettings().first().prayerSettings
                val nextPrayer = prayerRepository.getNextPrayer(
                    instant = Clock.System.now(),
                    madhab = settings.madhab,
                    calculationMethod = settings.calculationMethod,
                    location = Location(
                        longitude = settings.location.longitude,
                        latitude = settings.location.latitude,
                        country = "",
                        state = ""
                    ),
                    date = today
                )
                nextPrayer
            },
            onSuccess = { prayer ->
                val zone = TimeZone.currentSystemDefault()
                val nextUi = prayer.toPrayerUiState(zone).copy(isUpComing = true)

                updateState { current ->
                    val updatedList = current.prayers.map {
                        it.copy(isUpComing = (it.name == nextUi.name))
                    }

                    current.copy(
                        prayers = updatedList,
                        nextPrayer = nextUi
                    )

                }
                val nextInstantMillis = prayer.time.toEpochMilliseconds()
                startCountdown(nextInstantMillis)
            },
            onError = {
                updateState { currentState ->
                    currentState.copy(nextPrayer = HomeUiState.PrayerUiState())
                }
            }
        )


    }

    private fun startCountdown(nextPrayerMillis: Long) {
        countdownJob?.cancel()
        countdownJob = viewModelScope.launch(Dispatchers.IO) {
            while (true) {
                val diff = getTimeDifference(nextPrayerMillis)
                if (diff <= 0) {
                    _countdownTime.value = HomeUiState.TimeUiState("00", "00", "00")
                    getNextPrayer()
                    break
                }
                val time = convertMillisToHMS(diff)
                _countdownTime.value = HomeUiState.TimeUiState(
                    hours = time.first,
                    minutes = time.second,
                    seconds = time.third
                )
                delay(1000)
            }
        }
    }

    private fun updateCountdownUi(time: Triple<String, String, String>) {
        updateState { current ->
            current.copy(
                time = HomeUiState.TimeUiState(
                    hours = time.first,
                    minutes = time.second,
                    seconds = time.third
                )
            )
        }
    }

    private fun handleCountdownFinished() {
        updateState { current ->
            current.copy(
                time = HomeUiState.TimeUiState("00", "00", "00")
            )
        }
    }

    override fun onClickViewAll() {
        sendEffect(HomeEffect.NavigateToFullPrayersDetails)
    }

    override fun onClickSettings() {
        sendEffect(HomeEffect.NavigateToSettings)
    }

    override fun onClickQiblaDirection() {
        sendEffect(HomeEffect.NavigateToCalibrateDevice)
    }

    override fun onClickQuran() {
        sendEffect(HomeEffect.NavigateToQuran)
    }

    override fun onClickTilawah() {
        sendEffect(HomeEffect.NavigateToTilawah)
    }

    override fun onClickAzkar() {
        sendEffect(HomeEffect.NavigateToAzkar)
    }

}