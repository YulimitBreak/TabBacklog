package ui.page.summary

import common.DateUtils
import data.BookmarkRepository
import data.TabsRepository
import entity.Bookmark
import entity.BookmarkType
import kotlinx.browser.window
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class BookmarkSummaryModel(
    private val scope: CoroutineScope,
    private val bookmarkRepository: BookmarkRepository,
    private val tabsRepository: TabsRepository,
    private val bookmarkUpdateListener: BookmarkUpdateListener,
) {

    fun openManager() {
        scope.launch {
            tabsRepository.openManager()
            window.close()
        }
    }

    fun updateType(bookmark: Bookmark, type: BookmarkType) {
        scope.launch {
            val newBookmark = if (bookmark.isSaved) {
                bookmark.copy(type = type)
            } else {
                bookmark.copy(type = type, creationDate = DateUtils.now)
            }
            bookmarkRepository.saveBookmark(newBookmark)
            bookmarkUpdateListener.onUpdate(newBookmark)
        }
    }

    fun deleteBookmark(bookmark: Bookmark) {
        scope.launch {
            bookmarkRepository.deleteBookmark(bookmark.url)
            bookmarkUpdateListener.onUpdate(
                bookmark.copy(
                    type = BookmarkType.BACKLOG,
                    creationDate = null,
                )
            )
        }
    }

    fun updateFavorite(bookmark: Bookmark, isFavorite: Boolean) {
        scope.launch {
            val newBookmark = bookmark.copy(favorite = isFavorite)
            bookmarkRepository.saveBookmark(newBookmark)
            bookmarkUpdateListener.onUpdate(newBookmark)
        }
    }

    fun deleteReminder(bookmark: Bookmark) {
        scope.launch {
            val newBookmark = bookmark.copy(remindDate = null)
            bookmarkRepository.saveBookmark(newBookmark)
            bookmarkUpdateListener.onUpdate(newBookmark)
        }
    }

    fun deleteDeadline(bookmark: Bookmark) {
        scope.launch {
            val newBookmark = bookmark.copy(deadline = null)
            bookmarkRepository.saveBookmark(newBookmark)
            bookmarkUpdateListener.onUpdate(newBookmark)
        }
    }

    fun deleteExpiration(bookmark: Bookmark) {
        scope.launch {
            val newBookmark = bookmark.copy(remindDate = null)
            bookmarkRepository.saveBookmark(newBookmark)
            bookmarkUpdateListener.onUpdate(newBookmark)
        }
    }
}