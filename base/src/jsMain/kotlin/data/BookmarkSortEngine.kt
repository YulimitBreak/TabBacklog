package data

import com.juul.indexeddb.Key
import data.database.core.DatabaseHolder
import data.database.resolver.BookmarkDatabaseRetrieveResolver
import data.database.resolver.BookmarkListRetrieveResolver
import data.database.schema.TagSchema
import data.database.schema.extractObject
import data.database.util.DatabaseBookmarkScope
import entity.Bookmark
import entity.BookmarkSearchConfig
import entity.sort.BookmarkSort
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow

class BookmarkSortEngine(private val databaseHolder: DatabaseHolder) : DatabaseBookmarkScope {

    private val dbResolver = BookmarkDatabaseRetrieveResolver { databaseHolder.database() }

    fun readBookmarks(search: BookmarkSearchConfig, sort: BookmarkSort): Flow<Bookmark> =
        flow {
            emitAll(getBookmarkFlow(search, sort))
        }

    private suspend fun getBookmarkFlow(search: BookmarkSearchConfig, sort: BookmarkSort): Flow<Bookmark> {
        if (search.tags.isEmpty()) return resolveFromDatabase(search, sort)

        val mostLimitedTag = getMostLimitedTag(search.tags)
        if (mostLimitedTag.value > MAX_COUNT_TAG_LIMIT) return resolveFromDatabase(search, sort)

        val urls = databaseHolder.database().transaction(tagsSchema.storeName) {
            objectStore(tagsSchema.storeName)
                .index(TagSchema.Tag.name)
                .getAll(Key(mostLimitedTag.key))
                .map { tagsSchema.extract<String>(it, TagSchema.Url) }
                .distinct()
        }
        val bookmarksWithoutTags = databaseHolder.database().transaction(bookmarkSchema.storeName) {
            val store = objectStore(bookmarkSchema.storeName)
            urls.map { store.get(Key(it)) }.map { bookmarkSchema.extractObject(it) }
        }.filter { it.containsSearch(search.searchString) }
        val bookmarks = databaseHolder.database().transaction(tagsSchema.storeName, tagCountSchema.storeName) {
            bookmarksWithoutTags.map { bookmark ->
                bookmark.copy(tags = getTags(bookmark.url, withSorting = true))
            }
        }.filter { it.tags.containsAll(search.tags) }
        return BookmarkListRetrieveResolver(bookmarks).resolve(sort.retrieve)
    }

    private suspend fun resolveFromDatabase(search: BookmarkSearchConfig, sort: BookmarkSort) =
        dbResolver.resolve(sort.retrieve)
            .filter { it.containsSearch(search.searchString) }
            .filter { it.tags.containsAll(search.tags) }

    private suspend fun getMostLimitedTag(tags: Set<String>): Map.Entry<String, Int> {
        return databaseHolder.database().transaction(tagCountSchema.storeName) {
            getTagCount(tags)
        }.entries.minBy { it.value }
    }

    private fun Bookmark.containsSearch(searchString: String?): Boolean =
        searchString.isNullOrBlank() ||
                title.contains(searchString, ignoreCase = true) ||
                comment.contains(searchString, ignoreCase = true) ||
                url.contains(searchString, ignoreCase = true)

    companion object {
        private const val MAX_COUNT_TAG_LIMIT = 200
    }
}