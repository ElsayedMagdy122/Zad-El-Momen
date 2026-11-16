package dev.sayed.mehrabalmomen.presentation.screen.home

import dev.sayed.mehrabalmomen.presentation.base.BaseViewModel
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class HomeViewModel : BaseViewModel<HomeUiState, HomeEffect>(HomeUiState()), HomeInteractionListener {
    override fun onClickViewAll() {}
    override fun onClickSettings() {}
    override fun onClickQiblaDirection() {}
    override fun onClickQuran() {}
    override fun onClickTilawah() {}
}
