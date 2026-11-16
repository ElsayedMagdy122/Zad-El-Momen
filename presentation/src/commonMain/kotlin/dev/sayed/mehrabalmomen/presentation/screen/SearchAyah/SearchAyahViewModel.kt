package dev.sayed.mehrabalmomen.presentation.screen.SearchAyah
import dev.sayed.mehrabalmomen.presentation.base.BaseViewModel
class SearchAyahViewModel : BaseViewModel<SearchAyahUiState, SearchAyahEffect>(SearchAyahUiState(searchType = SearchType.QURAN))
