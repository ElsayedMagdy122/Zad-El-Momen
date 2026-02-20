package dev.sayed.mehrabalmomen.presentation.screen.bookmarks

import dev.sayed.mehrabalmomen.domain.repository.BookmarkRepository
import dev.sayed.mehrabalmomen.presentation.base.BaseViewModel

class BookMarkListViewModel(
    private val bookmarkRepository: BookmarkRepository,
) :
    BaseViewModel<BookmarkListUiState, Nothing>(BookmarkListUiState()) {

    fun getAllHistories() {
        tryToCall(
            onStart = {
                updateState { it.copy(isLoading = true) }
            },
            block = {
                val history = bookmarkRepository.getBookmarks()
                history
            },
            onSuccess = { bookmarks ->
                updateState {
                    it.copy(history = bookmarks.toUi(), isLoading = false)
                }
            },
            onError = {
                updateState {
                    it.copy(isLoading = false)
                }
            }
        )
    }

    fun deleteItem(surahId: Int, ayahId: Int) {
        tryToCall(
            block = { bookmarkRepository.removeBookmark(surahId, ayahId) },
            onSuccess = {
                getAllHistories()
            },
            onError = {}
        )
    }
}