package ui.page.summary

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import common.DateUtils
import data.BookmarkRepository
import data.BrowserInteractor
import entity.Bookmark
import entity.BookmarkType
import entity.core.Loadable
import entity.core.Url
import entity.core.load
import kotlinx.browser.window
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class BookmarkSummaryModel(
    private val url: Url?,
    private val scope: CoroutineScope,
    private val bookmarkRepository: BookmarkRepository,
    private val browserInteractor: BrowserInteractor,
) {

    var bookmark by mutableStateOf<Loadable<Bookmark>>(Loadable.Loading<Bookmark>())
        private set

    private fun CoroutineScope.loadBookmark(loader: suspend () -> Bookmark) =
        load(setter = { bookmark = it }, debounceTime = 200L, loader)

    private fun CoroutineScope.updateBookmark(action: suspend (Bookmark) -> Bookmark) {
        val bookmark = bookmark.value ?: return
        loadBookmark { action(bookmark) }
    }

    init {
        scope.loadBookmark {
            if (url == null) {
                bookmarkRepository.loadBookmarkForActiveTab()
            } else {
                bookmarkRepository.loadBookmark(url.url) ?: throw IllegalStateException("Bookmark not found")
            }
        }
    }

    fun openManager() {
        scope.launch {
            browserInteractor.openManager()
            window.close()
        }
    }

    fun updateType(type: BookmarkType) {
        scope.updateBookmark { bookmark ->
            val newBookmark = if (bookmark.isSaved) {
                bookmark.copy(type = type)
            } else {
                bookmark.copy(type = type, creationDate = DateUtils.now)
            }
            bookmarkRepository.saveBookmark(newBookmark)
            newBookmark
        }
    }

    fun deleteBookmark() {
        scope.updateBookmark { bookmark ->
            bookmarkRepository.deleteBookmark(bookmark.url)
            bookmark.copy(
                type = BookmarkType.BACKLOG,
                creationDate = null,
            )
        }
    }

    fun updateFavorite(isFavorite: Boolean) {
        scope.updateBookmark { bookmark ->
            val newBookmark = bookmark.copy(favorite = isFavorite)
            bookmarkRepository.saveBookmark(newBookmark)
            newBookmark
        }
    }

    fun deleteReminder() {
        scope.updateBookmark { bookmark ->
            val newBookmark = bookmark.copy(remindDate = null)
            bookmarkRepository.saveBookmark(newBookmark)
            newBookmark
        }
    }

    fun deleteDeadline() {
        scope.updateBookmark { bookmark ->
            val newBookmark = bookmark.copy(deadline = null)
            bookmarkRepository.saveBookmark(newBookmark)
            newBookmark
        }
    }

    fun deleteExpiration() {
        scope.updateBookmark { bookmark ->
            val newBookmark = bookmark.copy(expirationDate = null)
            bookmarkRepository.saveBookmark(newBookmark)
            newBookmark
        }
    }
}