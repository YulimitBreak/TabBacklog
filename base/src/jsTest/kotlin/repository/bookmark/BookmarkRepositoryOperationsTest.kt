package repository.bookmark

import common.DateUtils
import core.limit
import core.runTest
import core.timeLimit
import data.database.schema.TagSchema
import data.database.schema.extractObject
import io.kotest.assertions.withClue
import io.kotest.inspectors.forAll
import io.kotest.matchers.collections.shouldBeSortedWith
import io.kotest.matchers.ints.shouldBeExactly
import io.kotest.matchers.maps.shouldNotHaveKey
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldNotHave
import io.kotest.property.Arb
import io.kotest.property.arbitrary.element
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.single
import io.kotest.property.checkAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.minus
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BookmarkRepositoryOperationsTest : BookmarkRepositoryBaseTest() {

    @Test
    fun saveBookmark() = runTest {
        val holder = openDatabase()
        val repository = repository(holder)
        checkAll(timeLimit, bookmarkArb) { bookmark ->

            val oldTagCount = holder.database().transaction(tagCountSchema.storeName) {
                getTagCount(bookmark.tags)
            }

            repository.saveBookmark(bookmark)

            val result = holder.database()
                .transaction(bookmarkSchema.storeName, tagsSchema.storeName, tagCountSchema.storeName) {
                    loadBookmark(bookmark.url, withTags = true)
                }

            val newTagCount = holder.database().transaction(tagCountSchema.storeName) {
                getTagCount(bookmark.tags)
            }

            withClue("Retrieved bookmark should have same values it had during saving") {
                result shouldBeSame bookmark
            }
            withClue("Tag count should update after new bookmark") {
                oldTagCount.forAll { (k, v) ->
                    newTagCount.getValue(k) - 1 shouldBeExactly v
                }
            }
        }
    }

    @Test
    fun saveBookmark_update() = runTest {
        val holder = openDatabase()
        val repository = repository(holder)

        checkAll(timeLimit, bookmarkArb) { bookmarkSource ->
            val randomBookmark = holder.database()
                .transaction(bookmarkSchema.storeName, tagsSchema.storeName, tagCountSchema.storeName) {
                    bookmarkSchema.extractObject(objectStore(bookmarkSchema.storeName).getAll().random()).let {
                        it.copy(tags = getTags(it.url, withSorting = false))
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

            val allTags = (randomBookmark.tags + copiedBookmark.tags).distinct()
            val oldTagCount = holder.database().transaction(tagCountSchema.storeName) {
                getTagCount(allTags)
            }

            repository.saveBookmark(copiedBookmark)

            val newTagCount = holder.database().transaction(tagCountSchema.storeName) {
                getTagCount(allTags)
            }

            val result = holder.database()
                .transaction(bookmarkSchema.storeName, tagsSchema.storeName, tagCountSchema.storeName) {
                    loadBookmark(randomBookmark.url, withTags = true)
                }

            result shouldBeSame copiedBookmark
            result shouldNotHave bookmarkTagInvariantMatcher(randomBookmark)

            withClue("Tag count should be updated properly") {
                allTags.forAll { tag ->
                    when {
                        tag in randomBookmark.tags && tag !in copiedBookmark.tags -> {
                            val oldTagValue = oldTagCount.getValue(tag)
                            if (oldTagValue > 1) {
                                newTagCount.getValue(tag) + 1 shouldBeExactly oldTagValue
                            } else {
                                newTagCount shouldNotHaveKey tag
                            }
                        }

                        tag !in randomBookmark.tags && tag in copiedBookmark.tags -> {
                            newTagCount.getValue(tag) - 1 shouldBeExactly oldTagCount.getValue(tag)
                        }

                        else -> {
                            newTagCount.getValue(tag) shouldBeExactly oldTagCount.getValue(tag)
                        }
                    }
                }
            }
        }
    }

    @Test
    fun saveBookmark_deleteExpired() = runTest {
        val holder = openDatabase()
        val repository = repository(holder)
        checkAll(timeLimit, Arb.int(1, 365), bookmarkArb) { daysAgoExpired, newBookmark ->
            val expiredBookmark = bookmarkArb.single().copy(
                expirationDate = DateUtils.today - DatePeriod(days = daysAgoExpired)
            )
            holder.database().writeTransaction(bookmarkSchema.storeName) {
                saveBookmarkTransaction(expiredBookmark, withTags = false)
            }

            repository.saveBookmark(newBookmark)

            val result = holder.database().transaction(bookmarkSchema.storeName) {
                loadBookmark(expiredBookmark.url, withTags = false)
            }
            result.shouldBeNull()
        }
    }

    @Test
    fun saveBookmark_deleteExpired_expiredTagUpdate() = runTest {
        val holder = openDatabase()
        val repository = repository(holder)
        checkAll(
            timeLimit,
            Arb.int(1, 365),
            bookmarkArb.map { it.copy(tags = emptyList()) } // To not clash with expired bookmark tags
        ) { daysAgoExpired, newBookmark ->
            val expiredBookmark = bookmarkArb.single().copy(
                expirationDate = DateUtils.today - DatePeriod(days = daysAgoExpired)
            )
            holder.database()
                .writeTransaction(bookmarkSchema.storeName, tagsSchema.storeName, tagCountSchema.storeName) {
                    saveBookmarkTransaction(expiredBookmark, withTags = true)
                }

            val oldTagCount = holder.database().transaction(tagCountSchema.storeName) {
                getTagCount(expiredBookmark.tags)
            }

            repository.saveBookmark(newBookmark)

            val newTagCount = holder.database().transaction(tagCountSchema.storeName) {
                getTagCount(expiredBookmark.tags)
            }

            oldTagCount.forAll { (k, v) ->
                if (v > 1) {
                    newTagCount.getValue(k) + 1 shouldBeExactly v
                } else {
                    newTagCount shouldNotHaveKey k
                }
            }
        }
    }

    @Test
    fun deleteBookmark() = runTest {
        val holder = openDatabase(40)
        val repository = repository(holder)

        repeat(30) {
            val randomBookmarkUrl = holder.database().transaction(bookmarkSchema.storeName) {
                bookmarkSchema.extractObject(objectStore(bookmarkSchema.storeName).getAll().random()).url
            }

            repository.deleteBookmark(randomBookmarkUrl)

            val result = holder.database()
                .transaction(bookmarkSchema.storeName, tagsSchema.storeName, tagCountSchema.storeName) {
                    loadBookmark(randomBookmarkUrl, withTags = true)
                }

            result.shouldBeNull()
        }
    }

    @Test
    fun deleteBookmark_deleteExpired() = runTest {
        val holder = openDatabase(40)
        val repository = repository(holder)
        checkAll(timeLimit.limit(iterations = 30), Arb.int(1, 365)) { daysAgoExpired ->
            val expiredBookmark = bookmarkArb.single().copy(
                expirationDate = DateUtils.today - DatePeriod(days = daysAgoExpired)
            )
            val randomBookmark = holder.database().writeTransaction(bookmarkSchema.storeName) {
                val randomBookmark =
                    bookmarkSchema.extractObject(objectStore(bookmarkSchema.storeName).getAll().random()).url
                saveBookmarkTransaction(expiredBookmark, withTags = false)
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

        checkAll(timeLimit, bookmarkArb) { bookmark ->
            holder.database()
                .writeTransaction(bookmarkSchema.storeName, tagsSchema.storeName, tagCountSchema.storeName) {
                    saveBookmarkTransaction(bookmark, withTags = true)
                }

            val result = repository.loadBookmark(bookmark.url)

            result shouldBeSame bookmark
        }
    }

    @Test
    fun loadBookmark_tagsSorted() = runTest {
        val holder = openDatabase()
        val repository = repository(holder)
        val tagsSelection = holder.database().transaction(tagsSchema.storeName) {
            objectStore(tagsSchema.storeName).getAll().map {
                tagsSchema.extract(it) {
                    TagSchema.Url.value<String>() to TagSchema.Tag.value<String>()
                }
            }
        }
        val urls = tagsSelection.map { it.first }
        val tagCount = tagsSelection.map { it.second }.groupBy { it }.mapValues { it.value.size }
        checkAll(timeLimit.limit(iterations = urls.size), Arb.element(urls)) { url ->
            val bookmark = repository.loadBookmark(url) ?: return@checkAll

            bookmark.tags shouldBeSortedWith { a, b -> -tagCount.getValue(a).compareTo(tagCount.getValue(b)) }
        }
    }

    @Test
    fun loadBookmark_notFound() = runTest {
        val holder = openDatabase()
        val repository = repository(holder)

        checkAll(timeLimit, bookmarkArb) { bookmark ->
            val result = repository.loadBookmark(bookmark.url)
            result.shouldBeNull()
        }
    }
}