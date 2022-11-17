package data

import browser.tabs.Tab
import com.juul.indexeddb.Key
import data.database.core.DatabaseHolder
import data.database.schema.extractObject
import data.database.util.DatabaseBookmarkScope
import entity.Bookmark
import entity.BookmarkSearchConfig
import entity.BookmarkType
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

    private fun createNewBookmark(tab: Tab): Bookmark {
        console.log("Bookmark for ${tab.url} not found, creating new item")
        return Bookmark(
            url = tab.url ?: throw UnsupportedTabException(),
            title = tab.title ?: "",
            favicon = tab.favIconUrl,
            type = BookmarkType.BACKLOG,
            creationDate = null,
        )
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