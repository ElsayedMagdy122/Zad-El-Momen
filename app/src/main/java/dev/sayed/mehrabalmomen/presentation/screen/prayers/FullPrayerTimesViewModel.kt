@file:OptIn(ExperimentalTime::class)

package dev.sayed.mehrabalmomen.presentation.screen.prayers

import androidx.lifecycle.viewModelScope
import dev.sayed.mehrabalmomen.domain.entity.CalculationMethod
import dev.sayed.mehrabalmomen.domain.entity.Location
import dev.sayed.mehrabalmomen.domain.entity.Madhab
import dev.sayed.mehrabalmomen.domain.repository.PrayerRepository
import dev.sayed.mehrabalmomen.presentation.base.BaseViewModel
import dev.sayed.mehrabalmomen.presentation.screen.home.HomeUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class FullPrayerTimesViewModel(
    private val prayerRepository: PrayerRepository
) : BaseViewModel<FullPrayerTimesUiState, FullPrayerTimesEffect>(FullPrayerTimesUiState()) {
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
                val now = Clock.System.now().toEpochMilliseconds()
                val updatedPrayers = calculatePrayerProgress(prayerList, now)

                updateState { currentState ->
                    currentState.copy(prayers = updatedPrayers)
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
                    currentState.copy(nextPrayer = FullPrayerTimesUiState.PrayerUiState())
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
                            time = FullPrayerTimesUiState.TimeUiState("00", "00", "00")
                        )
                    }
                    break
                }

                val hours = diff / 1000 / 3600
                val minutes = (diff / 1000 % 3600) / 60
                val seconds = diff / 1000 % 60

                updateState { current ->
                    current.copy(
                        time = FullPrayerTimesUiState.TimeUiState(
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
    fun calculatePrayerProgress(
        prayers: List<FullPrayerTimesUiState.PrayerUiState>,
        nowMillis: Long
    ): List<FullPrayerTimesUiState.PrayerUiState> {
        return prayers.mapIndexed { index, prayer ->
            val prayerTimeMillis = prayer.instantTime?.toEpochMilliseconds() ?: 0L
            val previousPrayerTimeMillis = if (index > 0) prayers[index - 1].instantTime?.toEpochMilliseconds() ?: 0L else 0L

            val progress = when {
                nowMillis >= prayerTimeMillis -> 1f
                nowMillis < previousPrayerTimeMillis -> 0f
                else -> {
                    val elapsed = nowMillis - previousPrayerTimeMillis
                    val duration = prayerTimeMillis - previousPrayerTimeMillis
                    (elapsed.toFloat() / duration.toFloat()).coerceIn(0f, 1f)
                }
            }

            prayer.copy(progress = progress)
        }
    }
}