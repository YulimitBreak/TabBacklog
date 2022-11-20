package ui.page.bookmarklist

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import common.insertWithComparator
import common.produce
import common.receive
import data.BookmarkRepository
import data.BrowserInteractor
import entity.Bookmark
import entity.BookmarkSearchConfig
import entity.sort.BookmarkSort
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import ui.common.ext.apply

class BookmarkListModel(
    private val coroutineScope: CoroutineScope,
    private val bookmarkRepository: BookmarkRepository,
    private val browserInteractor: BrowserInteractor,
    onBookmarkSelectState: State<OnBookmarkSelect>,
) {

    private val onBookmarkSelect by onBookmarkSelectState

    var bookmarkListState by mutableStateOf(BookmarkListState(emptyList(), isLoading = false, reachedEnd = false))
        private set

    var searchConfig by mutableStateOf(BookmarkSearchViewConfig())
        private set

    private var bookmarkChannel: ReceiveChannel<Bookmark> = readBookmarks(searchConfig)

    // TODO multiselect
    var selectedBookmarks: List<String> by mutableStateOf(emptyList())
        private set
    private var lastClickedBookmarkUrl: String? = null

    init {
        coroutineScope.launch {
            browserInteractor.subscribeToDbUpdates().collect { changedUrl ->
                val changedBookmark = bookmarkRepository.loadBookmark(changedUrl)
                updateList { original ->
                    withNewEntry(original.filterNot { it.url == changedUrl }, changedBookmark)
                }
            }
        }
    }

    private fun updateList(update: (List<Bookmark>) -> List<Bookmark>) {
        bookmarkListState = bookmarkListState.copy(list = update(bookmarkListState.list))
    }

    private fun withNewEntry(list: List<Bookmark>, bookmark: Bookmark?): List<Bookmark> {
        if (bookmark == null) return list
        return if (bookmark.containsSearch(searchConfig.searchString) && bookmark.tags.containsAll(searchConfig.searchTags)) {
            list.insertWithComparator(bookmark, searchConfig.compiledSort)
        } else {
            list
        }
    }

    fun requestMoreBookmarks() {
        coroutineScope.launch {
            bookmarkListState = bookmarkListState.copy(isLoading = true)
            val newValues = bookmarkChannel.receive(BOOKMARK_PAGE_SIZE)
            bookmarkListState = bookmarkListState.copy(
                list = bookmarkListState.list + newValues,
                isLoading = false,
                reachedEnd = newValues.size < BOOKMARK_PAGE_SIZE
            )
        }
    }

    fun selectBookmark(bookmark: Bookmark) {
        this.selectedBookmarks = listOf(bookmark.url)
        onBookmarkSelect(listOf(bookmark)) // TODO
    }

    fun openBookmark(bookmark: Bookmark) {
        browserInteractor.openPage(bookmark.url)
    }

    private var cachedSearch: CachedSearch? = null

    fun onSearchConfigChange(event: BookmarkSearchViewEvent) {
        val cachedSearch = this.cachedSearch ?: CachedSearch(
            bookmarkListState.list,
            searchConfig.compiledSort,
            searchConfig.searchString,
            searchConfig.searchTags.toSet()
        ).also { this.cachedSearch = it }
        searchConfig = when (event) {
            is BookmarkSearchViewEvent.FavoriteFirstChange -> searchConfig.copy(favoriteFirst = event.favoriteFirst)
            is BookmarkSearchViewEvent.PresetChange -> searchConfig.copy(preset = event.preset)
            is BookmarkSearchViewEvent.SearchTextUpdate -> searchConfig.copy(searchString = event.text)
            is BookmarkSearchViewEvent.TypeFirstChange -> searchConfig.copy(typeFirst = event.type)
            is BookmarkSearchViewEvent.TagUpdate -> searchConfig.copy(
                searchTags = event.event.apply(
                    searchConfig.searchTags
                )
            )
        }
        if (searchConfig.compiledSort == cachedSearch.sort &&
            searchConfig.searchString.contains(cachedSearch.searchString) &&
            searchConfig.searchTags.containsAll(cachedSearch.searchTags)
        ) {
            // If current search is subset of cached search, we can just reuse results of cached search
            applySearchChange(searchConfig, cachedSearch.list.filter {
                it.containsSearch(searchConfig.searchString) && it.tags.containsAll(searchConfig.searchTags)
            })
        } else {
            // Otherwise cached search can be discarded
            this.cachedSearch = null
            applySearchChange(searchConfig)
        }
    }


    private fun applySearchChange(config: BookmarkSearchViewConfig, updatedList: List<Bookmark> = emptyList()) {
        bookmarkChannel.cancel()
        bookmarkChannel = readBookmarks(config, updatedList.size)
        bookmarkListState = BookmarkListState(list = updatedList, isLoading = false, reachedEnd = false)
    }

    private fun readBookmarks(config: BookmarkSearchViewConfig, skipCount: Int = 0): ReceiveChannel<Bookmark> =
        bookmarkRepository.readBookmarks(
            BookmarkSearchConfig(config.searchString, config.searchTags.toSet()),
            config.compiledSort
        ).drop(skipCount).produce(coroutineScope)

    data class BookmarkListState(val list: List<Bookmark>, val isLoading: Boolean, val reachedEnd: Boolean)

    private data class CachedSearch(
        val list: List<Bookmark>, val sort: BookmarkSort, val searchString: String, val searchTags: Set<String>
    )

    fun interface OnBookmarkSelect {
        operator fun invoke(bookmark: List<Bookmark>)
    }

    companion object {
        private const val BOOKMARK_PAGE_SIZE = 10
    }
}