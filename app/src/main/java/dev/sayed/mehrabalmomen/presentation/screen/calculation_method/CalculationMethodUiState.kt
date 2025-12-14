package dev.sayed.mehrabalmomen.presentation.screen.calculation_method

import dev.sayed.mehrabalmomen.R

data class CalculationMethodUiState(
    val selectedMethod : CalculationMethod = CalculationMethod.EGYPTIAN,
){
    enum class CalculationMethod(val value: Int) {
        MUSLIM_WORLD_LEAGUE(value = (R.string.muslim_world_league)),
        EGYPTIAN(value = (R.string.egyptian)),
        KARACHI(value = (R.string.karachi)),
        UMM_AL_QURA(value =(R.string.umm_al_qura)),
        DUBAI(value = (R.string.dubai)),
        QATAR(value = (R.string.qatar)),
        KUWAIT(value = (R.string.kuwait)),
        MOONSIGHTING_COMMITTEE(value =(R.string.moonsighting_committee)),
        SINGAPORE(value = (R.string.singapore)),
        NORTH_AMERICA(value = (R.string.north_america))
    }
}

