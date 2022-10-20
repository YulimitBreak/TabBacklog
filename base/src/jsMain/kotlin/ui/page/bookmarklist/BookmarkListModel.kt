package ui.page.bookmarklist

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import common.produce
import common.receive
import data.BookmarkRepository
import entity.Bookmark
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.launch

class BookmarkListModel(
    private val coroutineScope: CoroutineScope,
    private val bookmarkRepository: BookmarkRepository,
) {

    var bookmarkListState by mutableStateOf(BookmarkListState(emptyList(), isLoading = false, reachedEnd = false))
        private set

    private var bookmarkChannel: ReceiveChannel<Bookmark> = bookmarkRepository.readBookmarks().produce(coroutineScope)

    fun requestMoreBookmarks() {
        coroutineScope.launch {
            bookmarkListState = bookmarkListState.copy(isLoading = true)
            val newValues = bookmarkChannel.receive(BOOKMARK_PAGE_SIZE)
            console.log("Received bookmarks ${newValues.map { it.title }}")
            bookmarkListState = bookmarkListState.copy(
                list = bookmarkListState.list + newValues,
                isLoading = false,
                reachedEnd = newValues.size < BOOKMARK_PAGE_SIZE
            )
        }
    }

    data class BookmarkListState(val list: List<Bookmark>, val isLoading: Boolean, val reachedEnd: Boolean)

    companion object {
        private const val BOOKMARK_PAGE_SIZE = 10
    }
}