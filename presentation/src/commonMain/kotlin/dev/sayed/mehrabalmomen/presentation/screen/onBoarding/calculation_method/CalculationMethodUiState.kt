package dev.sayed.mehrabalmomen.presentation.screen.onBoarding.calculation_method

import dev.sayed.mehrabalmomen.designsystem.resources.Res
import dev.sayed.mehrabalmomen.designsystem.resources.*
import org.jetbrains.compose.resources.StringResource

data class CalculationMethodUiState(
    val selectedMethod : CalculationMethod = CalculationMethod.EGYPTIAN,
){
    enum class CalculationMethod(val value: StringResource) {
        MUSLIM_WORLD_LEAGUE(value = Res.string.muslim_world_league),
        EGYPTIAN(value = Res.string.egyptian),
        KARACHI(value = Res.string.karachi),
        UMM_AL_QURA(value = Res.string.umm_al_qura),
        DUBAI(value = Res.string.dubai),
        QATAR(value = Res.string.qatar),
        KUWAIT(value = Res.string.kuwait),
        MOONSIGHTING_COMMITTEE(value = Res.string.moonsighting_committee),
        SINGAPORE(value = Res.string.singapore),
        NORTH_AMERICA(value = Res.string.north_america)
    }
}
