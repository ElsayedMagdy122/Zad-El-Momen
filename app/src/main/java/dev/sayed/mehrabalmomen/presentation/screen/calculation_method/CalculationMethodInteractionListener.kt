package dev.sayed.mehrabalmomen.presentation.screen.calculation_method

import dev.sayed.mehrabalmomen.presentation.screen.madhab.MadhabUiState

interface CalculationMethodInteractionListener {
    fun onCalculationMethodClicked(method: CalculationMethodUiState.CalculationMethod)
    fun onClickContinue()
}