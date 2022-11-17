package repository.bookmark

import core.bookmarkArbShort
import core.runTest
import core.shuffle
import core.timeLimit
import data.BookmarkSortEngine
import data.database.schema.extractObject
import entity.Bookmark
import entity.BookmarkSearchConfig
import entity.sort.BookmarkSort
import entity.sort.SmartSort
import entity.sort.SortType
import io.kotest.assertions.withClue
import io.kotest.inspectors.forAll
import io.kotest.matchers.collections.shouldBeSortedWith
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.collections.shouldNotContainAll
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.*
import io.kotest.property.checkAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BookmarkSearchEngineTest : BookmarkDatabaseBaseTest() {

    @Test
    fun noTags() = runTest {
        val textPieces = Arb.string(1..100, Codepoint.az()).take(30).toList()
        val holder = openDatabase(
            40, bookmarkArbShort(
                title = Arb.element(textPieces),
                comment = Arb.element(textPieces)
            )
        )
        val allBookmarks =
            holder.database().transaction(bookmarkSchema.storeName) {
                objectStore(bookmarkSchema.storeName).getAll().map { bookmarkSchema.extractObject(it) }
            }
        val engine = BookmarkSortEngine(holder)
        checkAll(timeLimit, sortArb, searchArb(allBookmarks)) { sort, searchString ->
            val search = BookmarkSearchConfig(searchString, emptySet())

            val result = engine.readBookmarks(search, sort).toList()

            result shouldBeSortedWith sort
            withClue("All results should contain searched text") {
                result.forAll { bookmark ->
                    bookmark.containsSearch(searchString) shouldBe true
                }
            }

            val urlResults = result.map { it.url }
            val notResult = allBookmarks.filter { it.url !in urlResults }
            withClue("All bookmarks with searched text should be in result") {
                notResult.forAll { bookmark ->
                    if (searchString.isNotBlank()) {
                        bookmark.containsSearch(searchString) shouldBe false
                    }
                }
            }
        }
    }

    @Test
    fun withTags() = runTest {
        val textPieces = Arb.string(1..100, Codepoint.az()).take(30).toList()
        val holder = openDatabase(
            40, bookmarkArbShort(
                title = Arb.element(textPieces),
                comment = Arb.element(textPieces),
                tags = tags
            )
        )
        val allBookmarks =
            holder.database().transaction(bookmarkSchema.storeName, tagsSchema.storeName, tagCountSchema.storeName) {
                objectStore(bookmarkSchema.storeName).getAll().map { bookmarkSchema.extractObject(it) }
                    .map {
                        it.copy(tags = getTags(it.url, withSorting = true))
                    }
            }
        val tagArb = Arb.shuffle(tags).flatMap { Arb.subsequence(it) }.map { it.toSet() }
        val engine = BookmarkSortEngine(holder)
        checkAll(
            timeLimit,
            sortArb,
            searchArb(allBookmarks),
            tagArb,
        ) { sort, searchString, searchTags ->
            val search = BookmarkSearchConfig(searchString, searchTags)
            val result = engine.readBookmarks(search, sort).toList()
            result shouldBeSortedWith sort
            withClue("All results should contain searched text") {
                result.forAll {
                    it.containsSearch(searchString) shouldBe true
                }
            }
            withClue("All results should contain searched tags") {
                result.forAll {
                    it.tags shouldContainAll searchTags
                }
            }
            val urlResults = result.map { it.url }
            val notResult = allBookmarks.filter { it.url !in urlResults }
            withClue("All bookmarks with searched text should be in result") {
                notResult.forAll {
                    if (searchString.isNotBlank() && it.tags.containsAll(searchTags)) {
                        it.containsSearch(searchString) shouldBe false
                    }
                }
            }
            withClue("All bookmarks with searched tags should be in result") {
                notResult.forAll {
                    if (searchTags.isNotEmpty() && it.containsSearch(searchString)) {
                        it.tags shouldNotContainAll searchTags
                    }
                }
            }
        }
    }

    private fun searchArb(allBookmarks: List<Bookmark>): Arb<String> {
        val allText =
            allBookmarks.flatMap { listOf(it.url, it.title, it.comment) }.filter { it.isNotBlank() }.distinct()
        return arbitrary {
            val text = Arb.element(allText).bind()
            if (text.length < 2) return@arbitrary text
            val start = Arb.int(0, text.length - 2).bind()
            val end = Arb.int(start + 1, text.length).bind()
            text.substring(start..end)
        }.withEdgecases("", "   ")
    }

    private fun Bookmark.containsSearch(searchString: String) =
        searchString.isBlank() ||
                title.contains(searchString, ignoreCase = true) ||
                comment.contains(searchString, ignoreCase = true) ||
                url.contains(searchString, ignoreCase = true)

    private val derivativeSort = listOf<(BookmarkSort?) -> BookmarkSort>(
        SortType::FavoriteFirst,
        SortType::LibraryFirst,
        SortType::BacklogFirst,
        SortType::UnreachedReminderLast,
        SortType::DeadlineFirst,
        SortType::ReminderFirst,
        SortType::ExpiringSoonFirst,
    )
    private val terminalSort = listOf(
        SortType.Alphabetically(isReversed = true),
        SortType.Alphabetically(isReversed = false),
        SortType.CreationDate(unsavedFirst = true),
        SortType.CreationDate(unsavedFirst = false),
        SortType.CreationDate(unsavedFirst = true, isReversed = true),
        SortType.CreationDate(unsavedFirst = true, isReversed = false),
    )

    private val sortArb = arbitrary {
        val derivatives = Arb.subsequence(derivativeSort).shuffle().bind()
        val terminal = Arb.element(terminalSort).bind()

        derivatives.foldRight(terminal as BookmarkSort) { op, acc ->
            op(acc)
        }
    }.withEdgecases(SmartSort)
}