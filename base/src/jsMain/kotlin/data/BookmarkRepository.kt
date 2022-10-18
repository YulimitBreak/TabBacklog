package data

import browser.tabs.QueryQueryInfo
import browser.tabs.Tab
import com.juul.indexeddb.Key
import com.juul.indexeddb.WriteTransaction
import common.DateUtils
import data.database.core.DatabaseHolder
import data.database.core.DbSchema
import data.database.core.generate
import data.database.schema.BookmarkSchema
import data.database.schema.TagSchema
import entity.Bookmark
import entity.BookmarkType
import entity.error.UnsupportedTabException
import kotlinx.coroutines.await
import kotlinx.coroutines.flow.takeWhile
import kotlinx.datetime.toLocalDate
import kotlinx.datetime.toLocalDateTime

class BookmarkRepository(private val databaseHolder: DatabaseHolder) {

    suspend fun loadBookmarkForActiveTab(): Bookmark {
        val tab = browser.tabs.query(QueryQueryInfo {
            active = true
            currentWindow = true
        }).await().first()
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
        val bookmarkSchema = DbSchema<BookmarkSchema>()
        val tagsSchema = DbSchema<TagSchema>()
        databaseHolder.database().writeTransaction(bookmarkSchema.storeName, tagsSchema.storeName) {
            objectStore(bookmarkSchema.storeName).delete(Key(url))
            deleteTags(url)
            deleteExpiredBookmarks()
        }
    }

    suspend fun saveBookmark(bookmark: Bookmark) {
        console.log("Saving bookmark $bookmark")
        val bookmarkSchema = DbSchema<BookmarkSchema>()
        val tagsSchema = DbSchema<TagSchema>()
        databaseHolder.database().writeTransaction(bookmarkSchema.storeName, tagsSchema.storeName) {
            val url = bookmark.url
            objectStore(bookmarkSchema.storeName).put(bookmarkSchema.generate(bookmark))
            deleteTags(url)
            val tagsStore = objectStore(tagsSchema.storeName)
            bookmark.tags.forEach { tag ->
                tagsStore.put(
                    tagsSchema.generate(
                        mapOf(
                            TagSchema.Url to url,
                            TagSchema.Tag to tag,
                        )
                    )
                )
            }
            deleteExpiredBookmarks()
        }
    }


    suspend fun loadBookmark(url: String): Bookmark? {
        console.log("Trying to find bookmark by $url")
        val bookmarkSchema = DbSchema<BookmarkSchema>()
        val tagsSchema = DbSchema<TagSchema>()
        return databaseHolder.database().transaction(bookmarkSchema.storeName, tagsSchema.storeName) {
            val bookmarkEntity = objectStore(bookmarkSchema.storeName).get(Key(url)) ?: return@transaction null
            val bookmark = bookmarkSchema.extract(bookmarkEntity) { map ->
                Bookmark(
                    BookmarkSchema.Url.value(),
                    BookmarkSchema.Title.value(),
                    BookmarkSchema.Favicon.value(),
                    BookmarkSchema.Type.value<String>().let { BookmarkType.valueOf(it) },
                    BookmarkSchema.CreationDate.value<String?>()?.toLocalDateTime(),
                    BookmarkSchema.Deadline.value<String?>()?.toLocalDate(),
                    BookmarkSchema.RemindDate.value<String?>()?.toLocalDate(),
                    BookmarkSchema.ExpirationDate.value<String?>()?.toLocalDate(),
                    favorite = BookmarkSchema.Favorite.value(),
                    comment = BookmarkSchema.Comment.value() ?: ""
                )
            }
            console.log("Found bookmark: $bookmark")
            val tags = objectStore(tagsSchema.storeName).index(TagSchema.Url.name).getAll(Key(url)).map { entity ->
                tagsSchema.extract(entity) {
                    TagSchema.Tag.value<String>()
                }
            }.toSet()
            console.log("Found tags $tags")
            return@transaction bookmark.copy(tags = tags)
        }
    }

    private suspend fun WriteTransaction.deleteExpiredBookmarks() {
        val bookmarkSchema = DbSchema<BookmarkSchema>()
        objectStore(bookmarkSchema.storeName).index(BookmarkSchema.ExpirationDate.name).openCursor()
            .takeWhile { cursor ->
                val date = bookmarkSchema.extract(cursor.value) {
                    BookmarkSchema.ExpirationDate.value<String>().toLocalDate()
                }
                date.toEpochDays() < DateUtils.today.toEpochDays()
            }
            .collect { cursor ->
                val url = bookmarkSchema.extract(cursor.value) {
                    BookmarkSchema.Url.value<String>()
                }
                deleteTags(url)
                cursor.delete()
            }
    }

    private suspend fun WriteTransaction.deleteTags(url: String) {
        val tagsSchema = DbSchema<TagSchema>()
        objectStore(tagsSchema.storeName).index(TagSchema.Url.name).openCursor(Key(url)).collect { tagCursor ->
            tagCursor.delete()
        }
    }
}