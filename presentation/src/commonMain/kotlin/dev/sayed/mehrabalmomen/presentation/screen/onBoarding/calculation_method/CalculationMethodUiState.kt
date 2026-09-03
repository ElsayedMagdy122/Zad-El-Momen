package dev.sayed.mehrabalmomen.presentation.screen.onBoarding.calculation_method

import org.jetbrains.compose.resources.StringResource
import zad_el_momen.presentation.generated.resources.Res
import zad_el_momen.presentation.generated.resources.*

data class CalculationMethodUiState(
    val selectedMethod : CalculationMethod = CalculationMethod.EGYPTIAN,
){
    enum class CalculationMethod(val res: StringResource) {
        MUSLIM_WORLD_LEAGUE(Res.string.muslim_world_league),
        EGYPTIAN(Res.string.egyptian),
        KARACHI(Res.string.karachi),
        UMM_AL_QURA(Res.string.umm_al_qura),
        DUBAI(Res.string.dubai),
        QATAR(Res.string.qatar),
        KUWAIT(Res.string.kuwait),
        MOONSIGHTING_COMMITTEE(Res.string.moonsighting_committee),
        SINGAPORE(Res.string.singapore),
        NORTH_AMERICA(Res.string.north_america)
    }
}
