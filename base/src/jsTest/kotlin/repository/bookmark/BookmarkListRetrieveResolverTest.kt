package repository.bookmark

import core.bookmarkArbShort
import core.runTest
import core.shuffle
import core.timeLimit
import entity.sort.BookmarkListRetrieveResolver
import entity.sort.BookmarkSort
import entity.sort.SmartSort
import entity.sort.SortType
import io.kotest.matchers.collections.shouldBeSameSizeAs
import io.kotest.matchers.collections.shouldBeSortedWith
import io.kotest.property.Arb
import io.kotest.property.Exhaustive
import io.kotest.property.arbitrary.element
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.subsequence
import io.kotest.property.checkAll
import io.kotest.property.exhaustive.collection
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BookmarkListRetrieveResolverTest {

    @Test
    fun singleSortType() = runTest {
        val types = listOf(
            SortType.Alphabetically(),
            SortType.Alphabetically(isReversed = true),
            SortType.BacklogFirst(),
            SortType.CreationDate(unsavedFirst = true),
            SortType.CreationDate(unsavedFirst = false),
            SortType.CreationDate(unsavedFirst = true, isReversed = true),
            SortType.CreationDate(unsavedFirst = true, isReversed = false),
            SortType.DeadlineFirst(),
            SortType.ExpiringSoonFirst(),
            SortType.FavoriteFirst(),
            SortType.LibraryFirst(),
            SortType.ReminderFirst(),
            SortType.UnreachedReminderLast(),
        )
        checkAll(timeLimit, Exhaustive.collection(types), Arb.list(bookmarkArbShort())) { type, source ->
            val resolverSorted = type.retrieve.resolve(BookmarkListRetrieveResolver(source)).toList()
            resolverSorted shouldBeSameSizeAs source
            resolverSorted shouldBeSortedWith type
        }
    }

    @Test
    fun multiSort() = runTest {
        val derivatives = listOf<(BookmarkSort?) -> BookmarkSort>(
            SortType::FavoriteFirst,
            SortType::LibraryFirst,
            SortType::UnreachedReminderLast,
            SortType::DeadlineFirst,
            SortType::ReminderFirst,
            SortType::ExpiringSoonFirst,
        )
        val terminals = listOf(
            SortType.Alphabetically(isReversed = true),
            SortType.Alphabetically(isReversed = false),
            SortType.CreationDate(unsavedFirst = true),
            SortType.CreationDate(unsavedFirst = false),
            SortType.CreationDate(unsavedFirst = true, isReversed = true),
            SortType.CreationDate(unsavedFirst = true, isReversed = false),
        )
        checkAll(
            timeLimit,
            Arb.subsequence(derivatives).shuffle(),
            Arb.element(terminals),
            Arb.list(bookmarkArbShort())
        ) { der, term, source ->
            val sort = der.foldRight(term as BookmarkSort) { op, acc ->
                op(acc)
            }
            val resolverSorted = sort.retrieve.resolve(BookmarkListRetrieveResolver(source)).toList()
            resolverSorted shouldBeSameSizeAs source
            resolverSorted shouldBeSortedWith sort
        }
    }

    @Test
    fun smartSort() = runTest {
        checkAll(timeLimit, Arb.list(bookmarkArbShort())) { source ->
            val sort = SmartSort
            val resolverSorted = sort.retrieve.resolve(BookmarkListRetrieveResolver(source)).toList()

            resolverSorted shouldBeSameSizeAs source
            resolverSorted shouldBeSortedWith sort
        }
    }
}