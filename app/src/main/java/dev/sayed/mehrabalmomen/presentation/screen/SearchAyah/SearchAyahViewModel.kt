package dev.sayed.mehrabalmomen.presentation.screen.SearchAyah

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dev.sayed.mehrabalmomen.domain.repository.QuranRepository
import dev.sayed.mehrabalmomen.presentation.base.BaseViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch


class SearchAyahViewModel(
    private val repository: QuranRepository,
    savedStateHandle: SavedStateHandle
) : BaseViewModel<SearchAyahUiState, SearchAyahEffect>(
    SearchAyahUiState(
        searchType = savedStateHandle["type"] ?: SearchType.QURAN,
        surahId = savedStateHandle["surahId"],
        surahName = savedStateHandle["surahName"]
    )
),
    SearchAyahInteractionListener {
    private val _searchQuery = MutableStateFlow("")

    init {
        observeSearchQuery()
    }

    override fun onSearchQueryChange(query: String) {
        updateState { it.copy(searchQuery = query) }
        _searchQuery.value = query
    }


    @OptIn(FlowPreview::class)
    private fun observeSearchQuery() {
        viewModelScope.launch {
            _searchQuery
                .debounce(100L)
                .distinctUntilChanged()
                .collectLatest { query ->
                    performSearch(query)
                }
        }
    }

    private fun performSearch(query: String) {
        if (query.isBlank()) {
            updateState { it.copy(results = emptyList(), isLoading = false) }
            return
        }

        updateState { it.copy(isLoading = true) }

        tryToCall(
            block = {
                val ayahs = when (screenState.value.searchType) {
                    SearchType.QURAN ->
                        repository.searchInQuran(query.trim()).map {
                            AyahUi(
                                id = it.ayahNumber,
                                text = it.text,
                                surahId = it.surahNumber,
                                surahName = it.surahNameArabic
                            )
                        }

                    SearchType.SURAH ->
                        repository.searchInSurah(
                            surahNumber = screenState.value.surahId
                                ?: error("surahId is null in SURAH search"),
                            query = query.trim()
                        ).map {
                            AyahUi(
                                id = it.ayahNumber,
                                text = it.text,
                                surahId = it.surahNumber,
                                surahName = it.surahNameArabic
                            )
                        }
                }
                ayahs
            },
            onSuccess = { ayahs ->
                updateState { it.copy(results = ayahs, isLoading = false) }
            },
            onError = {
                updateState { it.copy(isLoading = false) }
            }
        )
    }

    override fun onBackClick() {
        sendEffect(SearchAyahEffect.NavigateBack)
    }
    override fun onAyahClick(ayah: AyahUi) {
        sendEffect(SearchAyahEffect.NavigateToSurah(ayah.surahId, ayah.surahName, ayah.id))
    }
}