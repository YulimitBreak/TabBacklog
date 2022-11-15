package repository.bookmark

import com.juul.indexeddb.Key
import com.juul.indexeddb.Transaction
import core.TestDatabaseHolder
import core.bookmarkArb
import core.onCleanup
import data.database.schema.extractObject
import data.database.util.DatabaseBookmarkScope
import entity.Bookmark
import entity.BookmarkType
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.string
import io.kotest.property.arbitrary.take
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

@OptIn(ExperimentalCoroutinesApi::class)
open class BookmarkDatabaseBaseTest : DatabaseBookmarkScope {

    val tags = Arb.string(minSize = 3, maxSize = 20).take(20).toList()
    val bookmarkArb = bookmarkArb(tags = tags)
    fun bookmarkTagInvariantMatcher(target: Bookmark?) = Matcher<Bookmark?> { source ->
        MatcherResult(
            source?.let { BookmarkTagInvariant(it) } == target?.let { BookmarkTagInvariant(it) },
            { "$source should be the same as $target" },
            { "$source should not be the same as $target" },
        )
    }

    infix fun Bookmark?.shouldBeSame(target: Bookmark) = this shouldBe bookmarkTagInvariantMatcher(target)

    protected suspend fun Transaction.loadBookmark(url: String, withTags: Boolean): Bookmark? {

        val bookmarkEntity = objectStore(bookmarkSchema.storeName).get(Key(url)) ?: return null
        val bookmark = bookmarkSchema.extractObject(bookmarkEntity)
        return if (withTags) {
            bookmark.copy(tags = getTagsTransaction(url, withSorting = false))
        } else bookmark
    }

    internal suspend fun TestScope.openDatabase(populateCount: Int = 40): TestDatabaseHolder {
        val holder = TestDatabaseHolder(
            "test_database",
            listOf(bookmarkSchema, tagsSchema)
        )
        onCleanup {
            holder.deleteDatabase()
        }
        holder.database().writeTransaction(bookmarkSchema.storeName, tagsSchema.storeName) {
            bookmarkArb.take(populateCount).forEach { bookmark ->
                saveBookmarkTransaction(bookmark, withTags = true)
            }
        }
        return holder
    }

    protected data class BookmarkTagInvariant(
        val url: String,
        val title: String,
        val favicon: String?,
        val type: BookmarkType,
        val creationDate: LocalDateTime?,
        val deadline: LocalDate? = null,
        val remindDate: LocalDate? = null,
        val expirationDate: LocalDate? = null,
        val tags: Set<String> = emptySet(),
        val favorite: Boolean = false,
        val comment: String = "",
    ) {
        constructor(bookmark: Bookmark) : this(
            bookmark.url,
            bookmark.title,
            bookmark.favicon,
            bookmark.type,
            bookmark.creationDate,
            bookmark.deadline,
            bookmark.remindDate,
            bookmark.expirationDate,
            bookmark.tags.toSet(),
            bookmark.favorite,
            bookmark.comment
        )
    }
}