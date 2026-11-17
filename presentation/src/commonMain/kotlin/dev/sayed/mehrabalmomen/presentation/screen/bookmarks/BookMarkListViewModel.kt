package dev.sayed.mehrabalmomen.presentation.screen.bookmarks

import dev.sayed.mehrabalmomen.domain.repository.quran.BookmarkRepository
import dev.sayed.mehrabalmomen.presentation.base.BaseViewModel

class BookMarkListViewModel(
    private val bookmarkRepository: BookmarkRepository,
) : BaseViewModel<BookmarkListUiState, Nothing>(BookmarkListUiState()) {

    fun getAllHistories() {
        tryToCall(
            onStart = {
                updateState { it.copy(isLoading = true) }
            },
            block = {
                bookmarkRepository.getBookmarks()
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
