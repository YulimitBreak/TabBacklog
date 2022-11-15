package data

import browser.tabs.Tab
import com.juul.indexeddb.Key
import common.isBeforeToday
import data.database.core.DatabaseHolder
import data.database.core.paginate
import data.database.schema.extractObject
import data.database.util.DatabaseBookmarkScope
import entity.Bookmark
import entity.BookmarkType
import entity.error.UnsupportedTabException
import kotlinx.coroutines.flow.*

class BookmarkRepository(
    private val databaseHolder: DatabaseHolder,
    private val browserInteractor: BrowserInteractor,
) : DatabaseBookmarkScope {


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
        databaseHolder.database().writeTransaction(bookmarkSchema.storeName, tagsSchema.storeName) {
            objectStore(bookmarkSchema.storeName).delete(Key(url))
            deleteTagsTransaction(url)
            deleteExpiredBookmarksTransaction()
            browserInteractor.sendUpdateMessage(url)
        }
    }

    suspend fun saveBookmark(bookmark: Bookmark) {
        console.log("Saving bookmark ${bookmark.url}")
        databaseHolder.database().writeTransaction(bookmarkSchema.storeName, tagsSchema.storeName) {
            saveBookmarkTransaction(bookmark, withTags = true)
            deleteExpiredBookmarksTransaction()
            browserInteractor.sendUpdateMessage(bookmark.url)
        }
    }


    suspend fun loadBookmark(url: String): Bookmark? {
        console.log("Trying to find bookmark by $url")
        return databaseHolder.database().transaction(bookmarkSchema.storeName, tagsSchema.storeName) {
            val bookmarkEntity = objectStore(bookmarkSchema.storeName).get(Key(url)) ?: return@transaction null
            val bookmark = bookmarkSchema.extractObject(bookmarkEntity)
            return@transaction bookmark.copy(tags = getTagsTransaction(url, withSorting = true))
        }
    }

    // TODO query params
    fun readBookmarks(): Flow<Bookmark> = flow {
        databaseHolder.database().paginate(bookmarkSchema.storeName) {
            bookmarkSchema.extractObject(it.value)
        }
            .filterNot { it.expirationDate?.isBeforeToday() == true }
            .map { bookmark ->
                databaseHolder.database().transaction(tagsSchema.storeName) {
                    bookmark.copy(tags = getTagsTransaction(bookmark.url, withSorting = true))
                }
            }
            .let { emitAll(it) }
    }


}