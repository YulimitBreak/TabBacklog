package ui.page.multisummary

import data.BookmarkRepository
import entity.BookmarkBundle
import entity.BookmarkType
import entity.MultiBookmarkSource
import entity.core.Loadable
import kotlinx.coroutines.CoroutineScope

class BookmarkMultiSummaryModel(
    private val target: MultiBookmarkSource,
    private val scope: CoroutineScope,
    private val bookmarkRepository: BookmarkRepository,
) {

    val bookmarks: Loadable<BookmarkBundle> get() = TODO()

    fun updateAllFavorite(favorite: Boolean) {
        TODO("Not yet implemented")
    }

    fun deleteAll() {
        TODO("Not yet implemented")
    }

    fun deleteReminder() {
        TODO("Not yet implemented")
    }

    fun deleteDeadline() {
        TODO("Not yet implemented")
    }

    fun deleteExpiration() {
        TODO("Not yet implemented")
    }

    fun updateType(type: BookmarkType) {
        TODO("Not yet implemented")
    }
}