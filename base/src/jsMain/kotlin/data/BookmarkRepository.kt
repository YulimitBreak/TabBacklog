package data

import browser.tabs.Tab
import com.juul.indexeddb.Key
import data.database.core.DatabaseHolder
import data.database.core.generate
import data.database.schema.TagSchema
import data.database.schema.extractObject
import data.database.util.DatabaseBookmarkScope
import entity.Bookmark
import entity.BookmarkSearchConfig
import entity.BookmarkSource
import entity.BookmarkType
import entity.BrowserTab
import entity.error.UnsupportedTabException
import entity.sort.BookmarkSort
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.toSet

class BookmarkRepository(
    private val databaseHolder: DatabaseHolder,
    private val browserInteractor: BrowserInteractor,
) : DatabaseBookmarkScope {

    private val sortEngine = BookmarkSortEngine(databaseHolder)

    private suspend fun loadBookmarkForActiveTab(): Bookmark {
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
        databaseHolder.database()
            .writeTransaction(bookmarkSchema.storeName, tagsSchema.storeName, tagCountSchema.storeName) {
                objectStore(bookmarkSchema.storeName).delete(Key(url))
                deleteTags(url, updateTagsCount = true)
                deleteExpiredBookmarks()
                browserInteractor.sendBookmarkUpdateMessage(url)
            }
    }

    suspend fun saveBookmark(bookmark: Bookmark) {
        databaseHolder.database()
            .writeTransaction(bookmarkSchema.storeName, tagsSchema.storeName, tagCountSchema.storeName) {
                saveBookmarkTransaction(bookmark, withTags = true)
                deleteExpiredBookmarks()
                browserInteractor.sendBookmarkUpdateMessage(bookmark.url)
            }
    }


    suspend fun loadBookmark(url: String): Bookmark? {
        return databaseHolder.database()
            .transaction(bookmarkSchema.storeName, tagsSchema.storeName, tagCountSchema.storeName) {
                val bookmarkEntity = objectStore(bookmarkSchema.storeName).get(Key(url)) ?: return@transaction null
                val bookmark = bookmarkSchema.extractObject(bookmarkEntity)
                return@transaction bookmark.copy(tags = getTags(url, withSorting = true))
            }
    }

    suspend fun getAllBookmarks(): List<Bookmark> = coroutineScope {
        val bookmarksAsync = async {
            databaseHolder.database().transaction(bookmarkSchema.storeName) {
                objectStore(bookmarkSchema.storeName).getAll().map { bookmarkSchema.extractObject(it) }
            }
        }
        val tagsAsync = async {
            databaseHolder.database().transaction(tagsSchema.storeName) {
                objectStore(tagsSchema.storeName).getAll().map {
                    tagsSchema.extract(it) {
                        TagSchema.Url.value<String>() to TagSchema.Tag.value<String>()
                    }
                }
            }
                .groupBy { it.first }
                .mapValues { entry -> entry.value.map { it.second } }
        }
        val bookmarks = bookmarksAsync.await()
        val tags = tagsAsync.await()

        bookmarks.map { bookmark ->
            bookmark.copy(
                tags = tags[bookmark.url].orEmpty()
            )
        }
    }

    suspend fun saveAllBookmarks(bookmarks: List<Bookmark>) {
        val urls = bookmarks.map { it.url }.toSet()
        databaseHolder.database()
            .writeTransaction(bookmarkSchema.storeName, tagsSchema.storeName, tagCountSchema.storeName) {
                val tagStore = objectStore(tagsSchema.storeName)
                val deletedTags = tagStore.openCursor(autoContinue = true)
                    .mapNotNull { cursor ->
                        val url = tagsSchema.extract<String>(cursor.value, TagSchema.Url)
                        if (url in urls) {
                            val tag = tagsSchema.extract<String>(cursor.value, TagSchema.Tag)
                            cursor.delete()
                            tag
                        } else {
                            null
                        }
                    }
                    .toSet()
                val affectedTags: Set<String> = bookmarks.flatMapTo(deletedTags.toMutableSet()) { it.tags }

                bookmarks.forEach { bookmark ->
                    objectStore(bookmarkSchema.storeName).put(bookmarkSchema.generate(bookmark))
                    bookmark.tags.forEach { tag ->
                        tagStore.put(
                            tagsSchema.generate(
                                mapOf(
                                    TagSchema.Tag to tag,
                                    TagSchema.Url to bookmark.url
                                )
                            )
                        )
                        Unit
                    }
                }
                affectedTags.forEach { tag ->
                    updateTagCount(tag)
                }
            }
        urls.forEach { browserInteractor.sendBookmarkUpdateMessage(it) }
    }

    fun readBookmarks(search: BookmarkSearchConfig, sort: BookmarkSort): Flow<Bookmark> =
        sortEngine.readBookmarks(search, sort)

}