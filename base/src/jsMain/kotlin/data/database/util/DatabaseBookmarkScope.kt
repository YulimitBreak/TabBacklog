package data.database.util

import com.juul.indexeddb.Key
import com.juul.indexeddb.Transaction
import com.juul.indexeddb.WriteTransaction
import common.DateUtils
import data.database.core.DbSchema
import data.database.core.generate
import data.database.schema.BookmarkSchema
import data.database.schema.TagSchema
import entity.Bookmark
import kotlinx.coroutines.flow.takeWhile
import kotlinx.datetime.toLocalDate

interface DatabaseBookmarkScope {

    val bookmarkSchema: DbSchema<BookmarkSchema> get() = DbSchema()
    val tagsSchema: DbSchema<TagSchema> get() = DbSchema()

    suspend fun WriteTransaction.saveBookmarkTransaction(bookmark: Bookmark, withTags: Boolean) {
        objectStore(bookmarkSchema.storeName).put(bookmarkSchema.generate(bookmark))
        if (withTags) {
            deleteTagsTransaction(bookmark.url)
            val tagsStore = objectStore(tagsSchema.storeName)
            bookmark.tags.forEach { tag ->
                tagsStore.put(
                    tagsSchema.generate(
                        mapOf(
                            TagSchema.Url to bookmark.url,
                            TagSchema.Tag to tag,
                        )
                    )
                )
            }
        }
    }


    suspend fun WriteTransaction.deleteExpiredBookmarksTransaction() {
        objectStore(bookmarkSchema.storeName).index(BookmarkSchema.ExpirationDate.name).openCursor()
            .takeWhile { cursor ->
                val date = bookmarkSchema.extract<String>(cursor.value, BookmarkSchema.ExpirationDate).toLocalDate()
                date.toEpochDays() < DateUtils.today.toEpochDays()
            }
            .collect { cursor ->
                val url = bookmarkSchema.extract<String>(cursor.value, BookmarkSchema.Url)
                cursor.delete()
                deleteTagsTransaction(url)
            }
    }

    suspend fun WriteTransaction.deleteTagsTransaction(url: String) {
        objectStore(tagsSchema.storeName).index(TagSchema.Url.name).openCursor(Key(url)).collect { tagCursor ->
            tagCursor.delete()
        }
    }

    suspend fun Transaction.getTagsTransaction(url: String, withSorting: Boolean): List<String> {
        val tagStore = objectStore(tagsSchema.storeName)

        return tagStore.index(TagSchema.Url.name).getAll(Key(url)).map { entity ->
            tagsSchema.extract<String>(entity, TagSchema.Tag)
        }.let { list ->
            if (withSorting) {
                list.map { tag ->
                    tag to tagStore.index(TagSchema.Tag.name).count(Key(tag))
                }
                    .sortedByDescending { it.second }
                    .map { it.first }
            } else list
        }
    }
}