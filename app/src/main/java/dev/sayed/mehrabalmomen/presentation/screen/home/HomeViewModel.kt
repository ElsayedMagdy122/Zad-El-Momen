@file:OptIn(ExperimentalTime::class)

package dev.sayed.mehrabalmomen.presentation.screen.home

import androidx.lifecycle.viewModelScope
import dev.sayed.mehrabalmomen.domain.model.PrayerAlarm
import dev.sayed.mehrabalmomen.domain.entity.Location
import dev.sayed.mehrabalmomen.domain.entity.Prayer
import dev.sayed.mehrabalmomen.domain.repository.LocationRepository
import dev.sayed.mehrabalmomen.domain.repository.PrayerRepository
import dev.sayed.mehrabalmomen.domain.repository.SettingsRepository
import dev.sayed.mehrabalmomen.presentation.base.BaseViewModel
import dev.sayed.mehrabalmomen.presentation.utils.convertMillisToHMS
import dev.sayed.mehrabalmomen.presentation.utils.getTimeDifference
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class HomeViewModel(
    private val prayerRepository: PrayerRepository,
    private val locationRepository: LocationRepository,
    private val settingsRepository: SettingsRepository,
) : BaseViewModel<HomeUiState, HomeEffect>(HomeUiState()), HomeInteractionListener {
    private var countdownJob: Job? = null
    private val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

    init {
        getDailyPrayers()

    }
    private fun getLocation() {
        tryToCall(
            block = {
                val location = locationRepository.getCountryAndState()
                location
            },
            onSuccess = {
                val location = HomeUiState.LocationUiState(
                    country = it.first,
                    city = it.second
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
    fun Prayer.toAlarm(): PrayerAlarm {
        return PrayerAlarm(
            id = this.name.ordinal,
            name = this.name,
            timeMillis = time.toEpochMilliseconds(),
            enabled = true
        )
    }
    private fun getDailyPrayers() {
        tryToCall(
            block = {
                val settings = settingsRepository.observeAppSettings().first()
                val prayers = prayerRepository.getDailyPrayers(
                    madhab = settings.madhab,
                    calculationMethod = settings.calculationMethod,
                    location = Location(
                        longitude = settings.longitude,
                        latitude = settings.latitude
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
                val settings = settingsRepository.observeAppSettings().first()
                val nextPrayer = prayerRepository.getNextPrayer(
                    instant = Clock.System.now(),
                    madhab = settings.madhab,
                    calculationMethod = settings.calculationMethod,
                    location = Location(
                        longitude = settings.longitude,
                        latitude = settings.latitude
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

        countdownJob = viewModelScope.launch {
            while (true) {
                val diff = getTimeDifference(nextPrayerMillis)

                if (diff <= 0) {
                    handleCountdownFinished()
                    getNextPrayer()
                    break
                }

                val time = convertMillisToHMS(diff)
                updateCountdownUi(time)

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

    override fun onClickQiblaDirection() {
        sendEffect(HomeEffect.NavigateToCalibrateDevice)
    }

}