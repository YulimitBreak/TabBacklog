package data.database.util

import com.juul.indexeddb.Key
import com.juul.indexeddb.Transaction
import com.juul.indexeddb.WriteTransaction
import common.DateUtils
import data.database.core.DbSchema
import data.database.core.generate
import data.database.schema.BookmarkSchema
import data.database.schema.TagCountSchema
import data.database.schema.TagSchema
import entity.Bookmark
import kotlinx.coroutines.flow.takeWhile
import kotlinx.datetime.toLocalDate

interface DatabaseBookmarkScope {

    val bookmarkSchema: DbSchema<BookmarkSchema> get() = DbSchema()
    val tagsSchema: DbSchema<TagSchema> get() = DbSchema()
    val tagCountSchema: DbSchema<TagCountSchema> get() = DbSchema()

    suspend fun WriteTransaction.saveBookmarkTransaction(bookmark: Bookmark, withTags: Boolean) {
        objectStore(bookmarkSchema.storeName).put(bookmarkSchema.generate(bookmark))
        if (withTags) {
            deleteTags(bookmark.url, updateTagsCount = true)
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
                updateTagCount(tag)
            }
        }
    }


    suspend fun WriteTransaction.deleteExpiredBookmarks() {

        val deletedTagUrls = mutableListOf<String>()
        objectStore(bookmarkSchema.storeName).index(BookmarkSchema.ExpirationDate.name).openCursor()
            .takeWhile { cursor ->
                val date = bookmarkSchema.extract<String>(cursor.value, BookmarkSchema.ExpirationDate).toLocalDate()
                date.toEpochDays() < DateUtils.today.toEpochDays()
            }
            .collect { cursor ->
                val url = bookmarkSchema.extract<String>(cursor.value, BookmarkSchema.Url)
                deletedTagUrls.add(url)
                cursor.delete()
            }
        deletedTagUrls.forEach { url ->
            deleteTags(url, true)
        }
    }

    suspend fun WriteTransaction.deleteTags(url: String, updateTagsCount: Boolean) {
        val updatedTags = mutableListOf<String>()
        objectStore(tagsSchema.storeName).index(TagSchema.Url.name).openCursor(Key(url)).collect { tagCursor ->
            if (updateTagsCount) {
                updatedTags.add(tagsSchema.extract(tagCursor.value, TagSchema.Tag))
            }
            tagCursor.delete()
        }
        updatedTags.forEach { tag ->
            updateTagCount(tag)
        }
    }

    suspend fun WriteTransaction.updateTagCount(tag: String) {
        val count = objectStore(tagsSchema.storeName).index(TagSchema.Tag.name).count(Key(tag))
        if (count > 0) {
            objectStore(tagCountSchema.storeName).put(
                tagCountSchema.generate(
                    mapOf(
                        TagCountSchema.Tag to tag,
                        TagCountSchema.Count to count,
                    )
                )
            )
        } else {
            objectStore(tagCountSchema.storeName).delete(Key(tag))
        }
    }

    suspend fun Transaction.getTags(url: String, withSorting: Boolean): List<String> {
        val tagStore = objectStore(tagsSchema.storeName)
        val tagCountStore = objectStore(tagCountSchema.storeName)

        return tagStore.index(TagSchema.Url.name).getAll(Key(url)).map { entity ->
            tagsSchema.extract<String>(entity, TagSchema.Tag)
        }.let { list ->
            if (withSorting) {
                list.map { tag ->
                    tag to tagCountSchema.extract<Int>(tagCountStore.get(Key(tag)), TagCountSchema.Count)
                }
                    .sortedByDescending { it.second }
                    .map { it.first }
            } else list
        }
    }
}