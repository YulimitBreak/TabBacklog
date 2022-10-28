package sort

import common.DateUtils
import common.isAfterToday
import core.bookmarkArb
import core.runTest
import core.timeLimit
import core.toLocalDateTime
import entity.Bookmark
import entity.BookmarkType
import entity.sort.SortType
import io.kotest.assertions.withClue
import io.kotest.common.KotestInternal
import io.kotest.inspectors.forAll
import io.kotest.matchers.collections.shouldBeSortedWith
import io.kotest.matchers.collections.shouldContainNoNulls
import io.kotest.matchers.collections.shouldContainOnlyNulls
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.*
import io.kotest.property.checkAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SortTypeTest {

    @Test
    fun creationDate_unsavedFirst() = runTest {
        val sortType = SortType.CreationDate(unsavedFirst = true)
        val arb = bookmarkArb(
            creationDate = Arb.datetime().map { it.toLocalDateTime() }.orNull(0.5)
        )
        checkAll(timeLimit, Arb.list(arb, 0..100)) { source ->
            val result = source.sortedWith(sortType)

            val saved = result.dropWhile { it.creationDate == null }

            withClue("All unsaved bookmarks should be at the start") {
                saved.map { it.creationDate }.shouldContainNoNulls()
            }
            saved.mapNotNull { it.creationDate } shouldBeSortedWith { a, b -> a.compareTo(b) }
        }
    }

    @Test
    fun creationDate_unsavedLast() = runTest {
        val sortType = SortType.CreationDate(unsavedFirst = false)
        val arb = bookmarkArb(
            creationDate = Arb.datetime().map { it.toLocalDateTime() }.orNull(0.5)
        )
        checkAll(timeLimit, Arb.list(arb, 0..100)) { source ->
            val result = source.sortedWith(sortType)

            val saved = result.takeWhile { it.creationDate != null }
            val unsaved = result.drop(saved.size)

            withClue("All unsaved bookmarks should be at the end") {
                unsaved.map { it.creationDate }.shouldContainOnlyNulls()
            }
            saved.mapNotNull { it.creationDate } shouldBeSortedWith { a, b -> a.compareTo(b) }
        }
    }

    @Test
    fun alphabetically() = runTest {
        val sortType = SortType.Alphabetically()
        checkAll(timeLimit, Arb.list(bookmarkArb(), 0..100)) { source ->
            val result = source.sortedWith(sortType)
            result shouldBeSortedWith { a, b -> a.title.compareTo(b.title) }
        }
    }

    @Test
    fun favoriteFirst() = runTest {
        val sortType = SortType.FavoriteFirst()
        checkAll(timeLimit, Arb.list(bookmarkArb(), 0..100)) { source ->
            val result = source.sortedWith(sortType)

            val notFavorite = result.dropWhile { it.favorite }

            withClue("All favorite bookmarks should be at the start") {
                notFavorite.forAll {
                    it.favorite shouldNotBe true
                }
            }
        }
    }

    @Test
    fun backlogFirst() = runTest {
        val sortType = SortType.BacklogFirst()
        checkAll(timeLimit, Arb.list(bookmarkArb(), 0..100)) { source ->
            val result = source.sortedWith(sortType)

            val notBacklog = result.dropWhile { it.type == BookmarkType.BACKLOG }

            withClue("All backlog bookmarks should be at the start") {
                notBacklog.forAll {
                    it.type shouldNotBe BookmarkType.BACKLOG
                }
            }
        }
    }

    @Test
    fun libraryFirst() = runTest {
        val sortType = SortType.LibraryFirst()
        checkAll(timeLimit, Arb.list(bookmarkArb(), 0..100)) { source ->
            val result = source.sortedWith(sortType)

            val notLibrary = result.dropWhile { it.type == BookmarkType.LIBRARY }

            withClue("All backlog bookmarks should be at the start") {
                notLibrary.forAll {
                    it.type shouldNotBe BookmarkType.LIBRARY
                }
            }
        }
    }

    @Test
    fun unreachedReminderLast() = runTest {
        val sortType = SortType.UnreachedReminderLast()
        checkAll(timeLimit, Arb.list(bookmarkArb(), 0..100)) { source ->
            val result = source.sortedWith(sortType)

            fun Bookmark.reminderNotReached(): Boolean {
                val remindDate = this.remindDate
                return remindDate != null && remindDate.isAfterToday()
            }

            val notUnreachedReminders = result.dropWhile { !it.reminderNotReached() }

            withClue("All unreached reminders should be at the end") {
                notUnreachedReminders.forAll {
                    it.remindDate.shouldNotBeNull()
                    it.reminderNotReached() shouldBe true
                }
            }
        }
    }

    @Test
    fun deadlineFirst() = runTest {
        val sortType = SortType.DeadlineFirst()
        checkAll(timeLimit, Arb.list(bookmarkArb(), 0..100)) { source ->
            val result = source.sortedWith(sortType)

            val deadlines = result.takeWhile { it.deadline != null }

            withClue("All deadlines should be at the start") {
                result.drop(deadlines.size).map { it.deadline }.shouldContainOnlyNulls()
            }
            deadlines shouldBeSortedWith { a, b -> a.deadline!!.compareTo(b.deadline!!) }
        }
    }

    @OptIn(KotestInternal::class)
    @Test
    fun reminderFirst() = runTest {
        val sortType = SortType.ReminderFirst()
        checkAll(timeLimit, Arb.list(bookmarkArb(), 0..10)) { source ->
            val result = source.sortedWith(sortType)

            fun Bookmark.reminderCheck(isReached: Boolean): Boolean {
                val reminder = this.remindDate
                return reminder != null && (reminder.isAfterToday() == !isReached)
            }

            val reachedReminders = result.takeWhile { it.reminderCheck(isReached = true) }
            val unreachedReminders = result.takeLastWhile { it.reminderCheck(isReached = false) }
            reachedReminders shouldBeSortedWith { a, b -> a.remindDate!!.compareTo(b.remindDate!!) }
            unreachedReminders shouldBeSortedWith { a, b -> a.remindDate!!.compareTo(b.remindDate!!) }
            withClue("All reached reminders should be at the start") {
                result.drop(reachedReminders.size).forAll { bookmark ->
                    bookmark.reminderCheck(isReached = true) shouldNotBe true
                }
            }
            withClue("All unreached reminders should be at the end") {
                result.dropLast(unreachedReminders.size).forAll { bookmark ->
                    bookmark.reminderCheck(isReached = false) shouldNotBe true
                }
            }
        }
    }

    @Test
    fun expiringSoonFirst() = runTest {
        val sortType = SortType.ExpiringSoonFirst()
        val arb = bookmarkArb(
            expirationDate = Arb.int(0, 30).map { DateUtils.today + DatePeriod(days = it) }.orNull(0.5)
        )
        checkAll(timeLimit, Arb.list(arb, 0..100)) { source ->
            val result = source.sortedWith(sortType)

            fun Bookmark.isExpiringSoon(): Boolean {
                val expirationDate = this.expirationDate
                return expirationDate != null && expirationDate.minus(1, DateTimeUnit.WEEK).isAfterToday().not()
            }

            val expired = result.takeWhile { it.isExpiringSoon() }
            expired shouldBeSortedWith { a, b -> a.expirationDate!!.compareTo(b.expirationDate!!) }
            withClue("Bookmarks expiring soon should be at the start of the list") {
                result.drop(expired.size).forAll {
                    it.isExpiringSoon() shouldNotBe true
                }
            }
        }
    }
}