package dev.sayed.mehrabalmomen.presentation.screen.calculation_method

import dev.sayed.mehrabalmomen.domain.entity.prayer.CalculationMethod

fun CalculationMethodUiState.CalculationMethod.toDomain():
        CalculationMethod {
    return when (this) {
        CalculationMethodUiState.CalculationMethod.MUSLIM_WORLD_LEAGUE -> CalculationMethod.MUSLIM_WORLD_LEAGUE
        CalculationMethodUiState.CalculationMethod.EGYPTIAN -> CalculationMethod.EGYPTIAN
        CalculationMethodUiState.CalculationMethod.KARACHI -> CalculationMethod.KARACHI
        CalculationMethodUiState.CalculationMethod.UMM_AL_QURA -> CalculationMethod.UMM_AL_QURA
        CalculationMethodUiState.CalculationMethod.DUBAI -> CalculationMethod.DUBAI
        CalculationMethodUiState.CalculationMethod.QATAR -> CalculationMethod.QATAR
        CalculationMethodUiState.CalculationMethod.KUWAIT -> CalculationMethod.KUWAIT
        CalculationMethodUiState.CalculationMethod.MOONSIGHTING_COMMITTEE -> CalculationMethod.MOONSIGHTING_COMMITTEE
        CalculationMethodUiState.CalculationMethod.SINGAPORE -> CalculationMethod.SINGAPORE
        CalculationMethodUiState.CalculationMethod.NORTH_AMERICA -> CalculationMethod.NORTH_AMERICA
    }
}