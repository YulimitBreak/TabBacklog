package ui.page.summary

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import common.DateUtils
import data.BookmarkRepository
import data.TabsRepository
import entity.*
import kotlinx.browser.window
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class BookmarkSummaryModel(
    private val url: Url?,
    private val scope: CoroutineScope,
    private val bookmarkRepository: BookmarkRepository,
    private val tabsRepository: TabsRepository,
) {

    var bookmark by mutableStateOf<Loadable<Bookmark>>(Loadable.Loading<Bookmark>())
        private set

    private fun CoroutineScope.loadBookmark(loader: suspend () -> Bookmark) =
        load(setter = { bookmark = it }, debounceTime = 200L, loader)

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
            tabsRepository.openManager()
            window.close()
        }
    }

    fun updateType(bookmark: Bookmark, type: BookmarkType) {
        scope.loadBookmark {
            val newBookmark = if (bookmark.isSaved) {
                bookmark.copy(type = type)
            } else {
                bookmark.copy(type = type, creationDate = DateUtils.now)
            }
            bookmarkRepository.saveBookmark(newBookmark)
            newBookmark
        }
    }

    fun deleteBookmark(bookmark: Bookmark) {
        scope.loadBookmark {
            bookmarkRepository.deleteBookmark(bookmark.url)
            bookmark.copy(
                type = BookmarkType.BACKLOG,
                creationDate = null,
            )
        }
    }

    fun updateFavorite(bookmark: Bookmark, isFavorite: Boolean) {
        scope.loadBookmark {
            val newBookmark = bookmark.copy(favorite = isFavorite)
            bookmarkRepository.saveBookmark(newBookmark)
            newBookmark
        }
    }

    fun deleteReminder(bookmark: Bookmark) {
        scope.loadBookmark {
            val newBookmark = bookmark.copy(remindDate = null)
            bookmarkRepository.saveBookmark(newBookmark)
            newBookmark
        }
    }

    fun deleteDeadline(bookmark: Bookmark) {
        scope.loadBookmark {
            val newBookmark = bookmark.copy(deadline = null)
            bookmarkRepository.saveBookmark(newBookmark)
            newBookmark
        }
    }

    fun deleteExpiration(bookmark: Bookmark) {
        scope.loadBookmark {
            val newBookmark = bookmark.copy(remindDate = null)
            bookmarkRepository.saveBookmark(newBookmark)
            newBookmark
        }
    }
}