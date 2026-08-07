package dev.sayed.mehrabalmomen.presentation.screen.quran.reciters_search

import androidx.lifecycle.viewModelScope
import dev.sayed.mehrabalmomen.domain.entity.quran.audio.QuranAudioReader
import dev.sayed.mehrabalmomen.domain.repository.quran.QuranAudioReadersRepository
import dev.sayed.mehrabalmomen.presentation.base.BaseViewModel
import dev.sayed.mehrabalmomen.presentation.screen.quran.reciters.toUiState
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class RecitersSearchViewModel(
    private val readersRepository: QuranAudioReadersRepository
) : BaseViewModel<RecitersSearchUiState, RecitersSearchEffect>(RecitersSearchUiState()) {

    private val _searchQuery = MutableStateFlow("")
    private var allReaders: List<QuranAudioReader> = emptyList()

    init {
        loadInitialData()
        observeSearchQuery()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            try {
                allReaders = readersRepository.getReaders()
            } catch (e: Exception) {
                allReaders = readersRepository.getDownloadedRecitersOnce()
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        updateState { it.copy(searchQuery = query) }
        _searchQuery.value = query
    }

    fun setLocale(isArabic: Boolean) {
        updateState { it.copy(isArabic = isArabic) }
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

        val filtered = allReaders.filter { reader ->
            reader.nameAr.contains(query, ignoreCase = true) ||
                    reader.nameEn.contains(query, ignoreCase = true)
        }.map { it.toUiState(isArabic = screenState.value.isArabic) }

        updateState { it.copy(results = filtered, isLoading = false) }
    }

    fun onBackClick() {
        sendEffect(RecitersSearchEffect.NavigateBack)
    }

    fun onReciterClick(readerId: Int, nameAr: String, nameEn: String) {
        sendEffect(
            RecitersSearchEffect.ReciterSelected(
                readerId = readerId,
                nameAr = nameAr,
                nameEn = nameEn
            )
        )
    }
}
