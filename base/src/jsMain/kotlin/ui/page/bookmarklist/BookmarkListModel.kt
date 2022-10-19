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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class BookmarkListModel(
    private val coroutineScope: CoroutineScope,
    private val bookmarkRepository: BookmarkRepository,
) {

    var bookmarkListState by mutableStateOf(BookmarkListState(emptyList(), isLoading = false, reachedEnd = false))
        private set

    private var bookmarkChannel: ReceiveChannel<Bookmark> = bookmarkRepository.readBookmarks().produce(coroutineScope)

    var numberListState by mutableStateOf(NumberListState(emptyList(), isLoading = false, reachedEnd = false))
    private var numberChannel: ReceiveChannel<Int> = flow {
        var i = 1
        while (true) {
            delay(100)
            console.log("Emitting $i")
            emit(i)
            i += 1
        }
    }.produce(coroutineScope)

    init {
        // requestMoreNumbers()
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

    fun requestMoreNumbers() {
        coroutineScope.launch {
            numberListState = numberListState.copy(isLoading = true)
            val newValues = numberChannel.receive(BOOKMARK_PAGE_SIZE)
            numberListState = numberListState.copy(
                list = numberListState.list + newValues,
                isLoading = false,
                reachedEnd = newValues.size < BOOKMARK_PAGE_SIZE
            )
        }
    }


    data class BookmarkListState(val list: List<Bookmark>, val isLoading: Boolean, val reachedEnd: Boolean)

    data class NumberListState(val list: List<Int>, val isLoading: Boolean, val reachedEnd: Boolean)

    companion object {
        private const val BOOKMARK_PAGE_SIZE = 10
    }
}