package ui.page.summary

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import common.DateUtils
import data.BookmarkRepository
import entity.Bookmark
import entity.BookmarkBundle
import entity.BookmarkType
import entity.MultiBookmarkSource
import entity.core.Loadable
import entity.core.load
import kotlinx.coroutines.CoroutineScope

class BookmarkMultiSummaryModel(
    private val target: MultiBookmarkSource,
    private val scope: CoroutineScope,
    private val bookmarkRepository: BookmarkRepository,
) {

    var bookmarks by mutableStateOf<Loadable<BookmarkBundle>>(Loadable.Loading())
        private set

    private fun CoroutineScope.loadBundle(loader: suspend () -> BookmarkBundle) =
        load(setter = { bookmarks = it }, debounceTime = 200L, loader)

    private fun CoroutineScope.updateBundle(action: suspend (BookmarkBundle) -> BookmarkBundle) {
        val bookmark = bookmarks.value ?: return
        loadBundle { action(bookmark) }
    }

    private fun CoroutineScope.updateBookmarks(
        onUpdated: suspend (Bookmark) -> Unit = { it.save() },
        action: suspend (Bookmark) -> Bookmark
    ) {
        updateBundle { bundle ->
            bundle.copy(
                bookmarks = bundle.bookmarks.map { source ->
                    val result = action(source)
                    if (source != result) onUpdated(result)
                    result
                }
            )
        }
    }

    private suspend fun Bookmark.save() {
        if (this.isSaved) {
            bookmarkRepository.saveBookmark(this)
        } else {
            bookmarkRepository.saveBookmark(copy(creationDate = DateUtils.now))
        }
    }

    init {
        scope.loadBundle {
            when (target) {
                is MultiBookmarkSource.Url -> target.urls.mapNotNull { url ->
                    bookmarkRepository.loadBookmark(url)
                }.let(::BookmarkBundle)
            }
        }
    }

    fun updateAllFavorite(favorite: Boolean) {
        scope.updateBookmarks {
            it.copy(favorite = favorite)
        }
    }

    fun deleteAll() {
        scope.updateBookmarks(onUpdated = { bookmarkRepository.deleteBookmark(it.url) }) {
            it.copy(creationDate = null)
        }
    }

    fun deleteReminder() {
        scope.updateBookmarks {
            it.copy(remindDate = null)
        }
    }

    fun deleteDeadline() {
        scope.updateBookmarks {
            it.copy(deadline = null)
        }
    }

    fun deleteExpiration() {
        scope.updateBookmarks {
            it.copy(expirationDate = null)
        }
    }

    fun updateType(type: BookmarkType) {
        scope.updateBookmarks {
            it.copy(type = type)
        }
    }
}