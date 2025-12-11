package dev.sayed.mehrabalmomen.presentation.screen.home

sealed interface  HomeEffect {
     object NavigateToFullPrayersDetails : HomeEffect
    object NavigateToCalibrateDevice : HomeEffect
}