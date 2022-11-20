package ui.page.bookmarklist

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import common.produce
import common.receive
import data.BookmarkRepository
import data.BrowserInteractor
import entity.Bookmark
import entity.BookmarkSearchConfig
import entity.BookmarkType
import entity.sort.BookmarkSort
import entity.sort.SmartSort
import entity.sort.SortType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.ReceiveChannel
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

    private var bookmarkChannel: ReceiveChannel<Bookmark> = bookmarkRepository.readBookmarks(
        BookmarkSearchConfig(), SmartSort
    ).produce(coroutineScope)

    var editedSearchConfig by mutableStateOf(BookmarkSearchViewConfig())
        private set

    private var appliedSearchConfig = editedSearchConfig

    // TODO multiselect
    var selectedBookmarkUrl: String? by mutableStateOf(null)
        private set

    init {
        coroutineScope.launch {
            browserInteractor.subscribeToDbUpdates().collect { changedUrl ->
                val changedBookmark = bookmarkRepository.loadBookmark(changedUrl)
                if (bookmarkListState.list.any { it.url == changedUrl }) {
                    // Old value was changed or deleted
                    bookmarkListState = bookmarkListState.copy(
                        list = bookmarkListState.list.mapNotNull { if (it.url == changedUrl) changedBookmark else it }
                    )
                } else if (changedBookmark != null) {
                    // New value was added
                    addNewEntry(changedBookmark)
                }
            }
        }
    }

    private fun addNewEntry(bookmark: Bookmark) {
        // TODO add bookmark into list if it matches search parameters, in correct place according to sorting method
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
        this.selectedBookmarkUrl = bookmark.url
        onBookmarkSelect(bookmark)
    }

    fun openBookmark(bookmark: Bookmark) {
        browserInteractor.openPage(bookmark.url)
    }

    fun onSearchConfigChange(event: BookmarkSearchViewEvent) {
        editedSearchConfig = when (event) {
            is BookmarkSearchViewEvent.FavoriteFirstChange -> editedSearchConfig.copy(favoriteFirst = event.favoriteFirst)
            is BookmarkSearchViewEvent.PresetChange -> editedSearchConfig.copy(preset = event.preset)
            is BookmarkSearchViewEvent.SearchTextUpdate -> editedSearchConfig.copy(searchString = event.text)
            is BookmarkSearchViewEvent.TypeFirstChange -> editedSearchConfig.copy(typeFirst = event.type)
            is BookmarkSearchViewEvent.TagUpdate -> editedSearchConfig.copy(
                searchTags = event.event.apply(
                    editedSearchConfig.searchTags
                )
            )
        }
    }

    fun onSearchConfigApply() {
        appliedSearchConfig = editedSearchConfig
        bookmarkChannel.cancel()
        bookmarkChannel = readBookmarks(appliedSearchConfig)
        bookmarkListState = BookmarkListState(emptyList(), isLoading = false, reachedEnd = false)
    }

    private fun readBookmarks(config: BookmarkSearchViewConfig): ReceiveChannel<Bookmark> =
        bookmarkRepository.readBookmarks(
            BookmarkSearchConfig(config.searchString, config.searchTags.toSet()),
            provideSort(config)
        ).produce(coroutineScope)

    private fun provideSort(config: BookmarkSearchViewConfig): BookmarkSort {
        var sort: BookmarkSort = when (val preset = config.preset) {
            is BookmarkSearchViewConfig.Preset.Alphabetically -> SortType.Alphabetically(isReversed = preset.isReversed)
            is BookmarkSearchViewConfig.Preset.CreationDate -> SortType.CreationDate(isReversed = preset.isReversed)
            is BookmarkSearchViewConfig.Preset.Smart -> SmartSort
        }
        sort = when (val type = config.typeFirst) {
            BookmarkType.LIBRARY -> SortType.LibraryFirst(sort)
            BookmarkType.BACKLOG -> SortType.BacklogFirst(sort)
            null -> sort
        }
        if (config.favoriteFirst) {
            sort = SortType.FavoriteFirst(sort)
        }
        return sort
    }

    data class BookmarkListState(val list: List<Bookmark>, val isLoading: Boolean, val reachedEnd: Boolean)

    companion object {
        private const val BOOKMARK_PAGE_SIZE = 10
    }

    fun interface OnBookmarkSelect {
        operator fun invoke(bookmark: Bookmark)
    }
}