package dev.sayed.mehrabalmomen.presentation.screen.home

sealed interface HomeEffect {
    object NavigateToFullPrayersDetails : HomeEffect
    object NavigateToSettings : HomeEffect
    object NavigateToCalibrateDevice : HomeEffect
    object NavigateToQuran : HomeEffect
    object NavigateToTilawah : HomeEffect
    object NavigateToAzkar : HomeEffect
}