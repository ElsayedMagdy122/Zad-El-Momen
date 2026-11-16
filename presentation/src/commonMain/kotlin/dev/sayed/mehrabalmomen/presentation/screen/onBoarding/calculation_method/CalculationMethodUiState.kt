package dev.sayed.mehrabalmomen.presentation.screen.onBoarding.calculation_method

import org.jetbrains.compose.resources.StringResource
import zadelmomen.presentation.generated.resources.Res
import zadelmomen.presentation.generated.resources.muslim_world_league
import zadelmomen.presentation.generated.resources.egyptian
import zadelmomen.presentation.generated.resources.karachi
import zadelmomen.presentation.generated.resources.umm_al_qura
import zadelmomen.presentation.generated.resources.dubai
import zadelmomen.presentation.generated.resources.qatar
import zadelmomen.presentation.generated.resources.kuwait
import zadelmomen.presentation.generated.resources.moonsighting_committee
import zadelmomen.presentation.generated.resources.singapore
import zadelmomen.presentation.generated.resources.north_america

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
