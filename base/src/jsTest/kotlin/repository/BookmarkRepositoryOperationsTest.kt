package repository

import common.DateUtils
import core.runTest
import data.database.schema.extractObject
import io.kotest.assertions.withClue
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldNotHave
import io.kotest.property.Arb
import io.kotest.property.RandomSource
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.minus
import kotlin.test.Test

class BookmarkRepositoryOperationsTest : BookmarkRepositoryBaseTest() {

    // TODO tag sorting after #5 implemented

    @Test
    fun saveBookmark() = runTest {
        val holder = openDatabase()
        val repository = repository(holder)
        checkAll(30, bookmarkArb) { bookmark ->
            repository.saveBookmark(bookmark)

            val result = holder.database().transaction(bookmarkSchema.storeName, tagSchema.storeName) {
                loadBookmark(bookmark.url, withTags = true)
            }
            withClue("Retrieved bookmark should have same values it had during saving") {
                result shouldBeSame bookmark
            }
        }
    }

    @Test
    fun saveBookmark_update() = runTest {
        val holder = openDatabase()
        val repository = repository(holder)

        checkAll(30, bookmarkArb) { bookmarkSource ->
            val randomBookmark = holder.database().transaction(bookmarkSchema.storeName, tagSchema.storeName) {
                bookmarkSchema.extractObject(objectStore(bookmarkSchema.storeName).getAll().random()).let {
                    it.copy(tags = getTags(it.url))
                }
            }

            val copiedBookmark = randomBookmark.copy(
                title = bookmarkSource.title,
                type = bookmarkSource.type,
                deadline = bookmarkSource.deadline,
                remindDate = bookmarkSource.remindDate,
                expirationDate = bookmarkSource.expirationDate,
                tags = bookmarkSource.tags,
                favorite = bookmarkSource.favorite,
                comment = bookmarkSource.comment,
            )

            repository.saveBookmark(copiedBookmark)

            val result = holder.database().transaction(bookmarkSchema.storeName, tagSchema.storeName) {
                loadBookmark(randomBookmark.url, withTags = true)
            }

            result shouldBeSame copiedBookmark
            result shouldNotHave bookmarkTagInvariantMatcher(randomBookmark)
        }
    }

    @Test
    fun saveBookmark_deleteExpired() = runTest {
        val holder = openDatabase()
        val repository = repository(holder)
        checkAll(30, Arb.int(1, 365), bookmarkArb) { daysAgoExpired, newBookmark ->
            val expiredBookmark = bookmarkArb.sample(RandomSource.default()).value.copy(
                expirationDate = DateUtils.today - DatePeriod(days = daysAgoExpired)
            )
            holder.database().writeTransaction(bookmarkSchema.storeName) {
                saveBookmark(expiredBookmark, withTags = false)
            }

            repository.saveBookmark(newBookmark)

            val result = holder.database().transaction(bookmarkSchema.storeName) {
                loadBookmark(expiredBookmark.url, withTags = false)
            }
            result.shouldBeNull()
        }
    }

    @Test
    fun deleteBookmark() = runTest {
        val holder = openDatabase()
        val repository = repository(holder)

        repeat(30) {
            val randomBookmarkUrl = holder.database().transaction(bookmarkSchema.storeName) {
                bookmarkSchema.extractObject(objectStore(bookmarkSchema.storeName).getAll().random()).url
            }

            repository.deleteBookmark(randomBookmarkUrl)

            val result = holder.database().transaction(bookmarkSchema.storeName, tagSchema.storeName) {
                loadBookmark(randomBookmarkUrl, withTags = true)
            }

            result.shouldBeNull()
        }
    }

    @Test
    fun deleteBookmark_deleteExpired() = runTest {
        val holder = openDatabase()
        val repository = repository(holder)
        checkAll(30, Arb.int(1, 365)) { daysAgoExpired ->
            val expiredBookmark = bookmarkArb.sample(RandomSource.default()).value.copy(
                expirationDate = DateUtils.today - DatePeriod(days = daysAgoExpired)
            )
            val randomBookmark = holder.database().writeTransaction(bookmarkSchema.storeName) {
                val randomBookmark =
                    bookmarkSchema.extractObject(objectStore(bookmarkSchema.storeName).getAll().random()).url
                saveBookmark(expiredBookmark, withTags = false)
                randomBookmark
            }

            repository.deleteBookmark(randomBookmark)

            val result = holder.database().transaction(bookmarkSchema.storeName) {
                loadBookmark(expiredBookmark.url, withTags = false)
            }
            result.shouldBeNull()
        }
    }

    @Test
    fun loadBookmark() = runTest {
        val holder = openDatabase()
        val repository = repository(holder)

        checkAll(30, bookmarkArb) { bookmark ->
            holder.database().writeTransaction(bookmarkSchema.storeName, tagSchema.storeName) {
                saveBookmark(bookmark, withTags = true)
            }

            val result = repository.loadBookmark(bookmark.url)

            result shouldBeSame bookmark
        }
    }

    @Test
    fun loadBookmark_notFound() = runTest {
        val holder = openDatabase()
        val repository = repository(holder)

        checkAll(bookmarkArb) { bookmark ->
            val result = repository.loadBookmark(bookmark.url)
            result.shouldBeNull()
        }
    }
}