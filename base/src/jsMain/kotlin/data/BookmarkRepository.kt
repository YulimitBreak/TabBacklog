package data

import browser.tabs.Tab
import com.juul.indexeddb.Key
import data.database.core.DatabaseHolder
import data.database.schema.extractObject
import data.database.util.DatabaseBookmarkScope
import entity.*
import entity.error.UnsupportedTabException
import entity.sort.BookmarkSort
import kotlinx.coroutines.flow.Flow

class BookmarkRepository(
    private val databaseHolder: DatabaseHolder,
    private val browserInteractor: BrowserInteractor,
) : DatabaseBookmarkScope {

    private val sortEngine = BookmarkSortEngine(databaseHolder)

    suspend fun loadBookmarkForActiveTab(): Bookmark {
        val tab = browserInteractor.getCurrentTab()
        return tab.url?.let { loadBookmark(it) } ?: createNewBookmark(tab)
    }

    private fun createNewBookmark(browserTab: BrowserTab) = createNewBookmark(
        browserTab.url, browserTab.title, browserTab.favIcon
    )

    private fun createNewBookmark(tab: Tab): Bookmark = createNewBookmark(
        url = tab.url ?: throw UnsupportedTabException(),
        title = tab.title ?: "",
        favicon = tab.favIconUrl,
    )

    private fun createNewBookmark(url: String, title: String, favicon: String?) =
        Bookmark(
            url = url,
            title = title,
            favicon = favicon,
            type = BookmarkType.BACKLOG,
            creationDate = null
        )

    suspend fun loadBookmark(source: BookmarkSource) = when (source) {
        is BookmarkSource.CurrentTab -> loadBookmarkForActiveTab()
        is BookmarkSource.SelectedBookmark -> source.bookmark
        is BookmarkSource.Url -> loadBookmark(source.url) ?: throw IllegalStateException("Bookmark Not Found")
        is BookmarkSource.Tab -> loadBookmark(source.browserTab.url) ?: createNewBookmark(source.browserTab)
    }

    suspend fun deleteBookmark(url: String) {
        console.log("Deleting bookmark for $url")
        databaseHolder.database()
            .writeTransaction(bookmarkSchema.storeName, tagsSchema.storeName, tagCountSchema.storeName) {
                objectStore(bookmarkSchema.storeName).delete(Key(url))
                deleteTags(url, updateTagsCount = true)
                deleteExpiredBookmarks()
                browserInteractor.sendUpdateMessage(url)
            }
    }

    suspend fun saveBookmark(bookmark: Bookmark) {
        console.log("Saving bookmark ${bookmark.url}")
        databaseHolder.database()
            .writeTransaction(bookmarkSchema.storeName, tagsSchema.storeName, tagCountSchema.storeName) {
                saveBookmarkTransaction(bookmark, withTags = true)
                deleteExpiredBookmarks()
                browserInteractor.sendUpdateMessage(bookmark.url)
            }
    }


    suspend fun loadBookmark(url: String): Bookmark? {
        console.log("Trying to find bookmark by $url")
        return databaseHolder.database()
            .transaction(bookmarkSchema.storeName, tagsSchema.storeName, tagCountSchema.storeName) {
                val bookmarkEntity = objectStore(bookmarkSchema.storeName).get(Key(url)) ?: return@transaction null
                val bookmark = bookmarkSchema.extractObject(bookmarkEntity)
                return@transaction bookmark.copy(tags = getTags(url, withSorting = true))
            }
    }

    fun readBookmarks(search: BookmarkSearchConfig, sort: BookmarkSort): Flow<Bookmark> =
        sortEngine.readBookmarks(search, sort)

}