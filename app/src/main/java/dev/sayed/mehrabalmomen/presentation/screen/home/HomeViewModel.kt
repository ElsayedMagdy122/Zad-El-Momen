@file:OptIn(ExperimentalTime::class)

package dev.sayed.mehrabalmomen.presentation.screen.home

import dev.sayed.mehrabalmomen.domain.entity.CalculationMethod
import dev.sayed.mehrabalmomen.domain.entity.Location
import dev.sayed.mehrabalmomen.domain.entity.Madhab
import dev.sayed.mehrabalmomen.domain.repository.PrayerRepository
import dev.sayed.mehrabalmomen.presentation.base.BaseViewModel
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlin.time.ExperimentalTime

class HomeViewModel(
    private val prayerRepository: PrayerRepository
) : BaseViewModel<HomeUiState, HomeEffect>(HomeUiState()) {

    init {
        getDailyPrayers()
    }

    fun getDailyPrayers() {
        tryToCall(
            block = {
                val prayers = prayerRepository.getDailyPrayers(
                    madhab = Madhab.SHAFI,
                    calculationMethod = CalculationMethod.MUSLIM_WORLD_LEAGUE,
                    location = Location(latitude = 30.033333, longitude = 31.233334),
                    date = LocalDate(2025, 12, 7)
                )
                val zone = TimeZone.currentSystemDefault()
                prayers.map { it.toPrayerUiState(zone = zone) }
            },
            onSuccess = { prayerList ->
                updateState { currentState ->
                    currentState.copy(prayers = prayerList)
                }
                println("SAYEd : getDailyPrayers")
            },
            onError = {}
        )


    }

}