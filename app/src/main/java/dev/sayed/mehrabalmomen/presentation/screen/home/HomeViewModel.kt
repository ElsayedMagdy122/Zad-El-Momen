@file:OptIn(ExperimentalTime::class)

package dev.sayed.mehrabalmomen.presentation.screen.home

import androidx.lifecycle.viewModelScope
import dev.sayed.mehrabalmomen.domain.entity.CalculationMethod
import dev.sayed.mehrabalmomen.domain.entity.Location
import dev.sayed.mehrabalmomen.domain.entity.Madhab
import dev.sayed.mehrabalmomen.domain.repository.PrayerRepository
import dev.sayed.mehrabalmomen.presentation.base.BaseViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class HomeViewModel(
    private val prayerRepository: PrayerRepository
) : BaseViewModel<HomeUiState, HomeEffect>(HomeUiState()) {
    private var countdownJob: Job? = null
    init {
        getDailyPrayers()
      //  getNextPrayer()
    }

    fun getDailyPrayers() {
        tryToCall(
            block = {
                val prayers = prayerRepository.getDailyPrayers(
                    madhab = Madhab.SHAFI,
                    calculationMethod = CalculationMethod.MUSLIM_WORLD_LEAGUE,
                    location = Location(latitude = 30.033333, longitude = 31.233334),
                    date = LocalDate(2025, 12, 8)
                )
                val zone = TimeZone.currentSystemDefault()
                prayers.map { it.toPrayerUiState(zone = zone) }
            },
            onSuccess = { prayerList ->
                updateState { currentState ->
                    currentState.copy(prayers = prayerList)
                }
                getNextPrayer()
            },
            onError = {}
        )


    }

    fun getNextPrayer() {
        tryToCall(
            block = {
                val nextPrayer = prayerRepository.getNextPrayer(
                    instant = Clock.System.now(),
                    madhab = Madhab.SHAFI,
                    calculationMethod = CalculationMethod.MUSLIM_WORLD_LEAGUE,
                    location = Location(latitude = 30.033333, longitude = 31.233334),
                    date = LocalDate(2025, 12, 8)
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
                val now = System.currentTimeMillis()
                val diff = nextPrayerMillis - now

                if (diff <= 0) {
                    updateState { current ->
                        current.copy(
                            time = HomeUiState.TimeUiState("00", "00", "00")
                        )
                    }
                    break
                }

                val hours = diff / 1000 / 3600
                val minutes = (diff / 1000 % 3600) / 60
                val seconds = diff / 1000 % 60

                updateState { current ->
                    current.copy(
                        time = HomeUiState.TimeUiState(
                            hours = String.format("%02d", hours),
                            minutes = String.format("%02d", minutes),
                            seconds = String.format("%02d", seconds)
                        )
                    )
                }

                delay(1000)
            }
        }
    }

}