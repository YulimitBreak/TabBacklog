package repository.bookmark

import com.juul.indexeddb.Database
import core.runTest
import core.shuffle
import core.timeLimit
import data.database.schema.extractObject
import entity.Bookmark
import entity.sort.BookmarkDatabaseRetrieveResolver
import entity.sort.BookmarkSort
import entity.sort.SmartSort
import entity.sort.SortType
import io.kotest.matchers.collections.shouldBeSameSizeAs
import io.kotest.matchers.collections.shouldBeSortedWith
import io.kotest.property.Arb
import io.kotest.property.Exhaustive
import io.kotest.property.arbitrary.element
import io.kotest.property.arbitrary.subsequence
import io.kotest.property.checkAll
import io.kotest.property.exhaustive.collection
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BookmarkDatabaseRetrieveResolverTest : BookmarkDatabaseBaseTest() {

    private suspend fun Database.getAllBookmarks(): List<Bookmark> =
        transaction(bookmarkSchema.storeName, tagsSchema.storeName, tagCountSchema.storeName) {
            objectStore(bookmarkSchema.storeName).getAll()
                .map { bookmarkSchema.extractObject(it) }
                .map { it.copy(tags = getTags(it.url, true)) }
        }

    @Test
    fun singleSort() = runTest {
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
        val holder = openDatabase(20)
        val source = holder.database().getAllBookmarks()
        checkAll(timeLimit, Exhaustive.collection(types)) { type ->
            val resolverSorted = type.retrieve.resolve(BookmarkDatabaseRetrieveResolver(holder::database)).toList()
            resolverSorted shouldBeSameSizeAs source
            resolverSorted shouldBeSortedWith type
        }
    }

    @Test
    fun multiSort() = runTest {
        val derivatives = listOf<(BookmarkSort?) -> BookmarkSort>(
            SortType::FavoriteFirst,
            SortType::LibraryFirst,
            SortType::BacklogFirst,
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

        val holder = openDatabase(20)
        val source = holder.database().getAllBookmarks()
        checkAll(
            timeLimit,
            Arb.subsequence(derivatives).shuffle(),
            Arb.element(terminals)
        ) { der, term ->
            val sort = der.foldRight(term as BookmarkSort) { op, acc ->
                op(acc)
            }
            val resolverSorted = sort.retrieve.resolve(BookmarkDatabaseRetrieveResolver(holder::database)).toList()
            resolverSorted shouldBeSameSizeAs source
            resolverSorted shouldBeSortedWith sort
        }
    }

    @Test
    fun smartSort() = runTest {

        val holder = openDatabase(20)
        val source = holder.database().getAllBookmarks()
        val sort = SmartSort
        val resolverSorted = sort.retrieve.resolve(BookmarkDatabaseRetrieveResolver(holder::database)).toList()

        resolverSorted shouldBeSameSizeAs source
        resolverSorted shouldBeSortedWith sort
    }
}