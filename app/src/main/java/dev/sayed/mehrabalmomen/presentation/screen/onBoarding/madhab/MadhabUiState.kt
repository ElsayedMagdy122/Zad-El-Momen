package dev.sayed.mehrabalmomen.presentation.screen.onBoarding.madhab

import dev.sayed.mehrabalmomen.R

data class MadhabUiState(
    val selectedMadhab : MadhabState = MadhabState.SHAFI,
){
    enum class MadhabState(val value: Int) {
        SHAFI(value =R.string.shafi),
        HANAFI(value =R.string.hanafi)
    }
}
