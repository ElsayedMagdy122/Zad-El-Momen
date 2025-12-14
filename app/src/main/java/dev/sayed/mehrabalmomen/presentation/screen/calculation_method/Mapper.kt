package dev.sayed.mehrabalmomen.presentation.screen.calculation_method

fun CalculationMethodUiState.CalculationMethod.toDomain():
        dev.sayed.mehrabalmomen.domain.entity.CalculationMethod {
    return when (this) {
        CalculationMethodUiState.CalculationMethod.MUSLIM_WORLD_LEAGUE -> dev.sayed.mehrabalmomen.domain.entity.CalculationMethod.MUSLIM_WORLD_LEAGUE
        CalculationMethodUiState.CalculationMethod.EGYPTIAN -> dev.sayed.mehrabalmomen.domain.entity.CalculationMethod.EGYPTIAN
        CalculationMethodUiState.CalculationMethod.KARACHI -> dev.sayed.mehrabalmomen.domain.entity.CalculationMethod.KARACHI
        CalculationMethodUiState.CalculationMethod.UMM_AL_QURA -> dev.sayed.mehrabalmomen.domain.entity.CalculationMethod.UMM_AL_QURA
        CalculationMethodUiState.CalculationMethod.DUBAI -> dev.sayed.mehrabalmomen.domain.entity.CalculationMethod.DUBAI
        CalculationMethodUiState.CalculationMethod.QATAR -> dev.sayed.mehrabalmomen.domain.entity.CalculationMethod.QATAR
        CalculationMethodUiState.CalculationMethod.KUWAIT -> dev.sayed.mehrabalmomen.domain.entity.CalculationMethod.KUWAIT
        CalculationMethodUiState.CalculationMethod.MOONSIGHTING_COMMITTEE -> dev.sayed.mehrabalmomen.domain.entity.CalculationMethod.MOONSIGHTING_COMMITTEE
        CalculationMethodUiState.CalculationMethod.SINGAPORE -> dev.sayed.mehrabalmomen.domain.entity.CalculationMethod.SINGAPORE
        CalculationMethodUiState.CalculationMethod.NORTH_AMERICA -> dev.sayed.mehrabalmomen.domain.entity.CalculationMethod.NORTH_AMERICA
    }
}