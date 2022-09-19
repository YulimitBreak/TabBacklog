package data

import browser.tabs.QueryQueryInfo
import browser.tabs.Tab
import entity.Bookmark
import entity.BookmarkStatus
import entity.error.UnsupportedTabException
import kotlinx.coroutines.await
import kotlinx.coroutines.delay

class BookmarkRepository {

    suspend fun loadBookmarkForActiveTab(): Bookmark {
        val tab = browser.tabs.query(QueryQueryInfo {
            active = true
            currentWindow = true
        }).await().first()
        return loadBookmark(tab) ?: Bookmark(
            url = tab.url ?: throw UnsupportedTabException(),
            title = tab.title ?: "",
            favicon = tab.favIconUrl,
            status = BookmarkStatus.Backlog,
            creationDate = null
        )
    }

    suspend fun loadBookmark(tab: Tab): Bookmark? {
        // TODO load from DB
        delay(1000)
        return null
    }
}