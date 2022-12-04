package ui.page.tablist

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import data.BookmarkRepository
import data.BrowserInteractor
import entity.Tab
import kotlinx.coroutines.CoroutineScope

class TabListModel(
    private val coroutineScope: CoroutineScope,
    private val browserInteractor: BrowserInteractor,
    private val bookmarkRepository: BookmarkRepository,
    onLinkSelectState: State<OnLinkSelect>,
) {

    private val onLinkSelect by onLinkSelectState

    var selectedTabs by mutableStateOf(emptySet<Int>())
        private set

    private var listState by mutableStateOf(ListState(emptyList(), isLoading = false, reachedEnd = false))
    val tabs get() = listState.list
    val isLoading get() = listState.isLoading
    val reachedEnd get() = listState.reachedEnd

    var multiSelectMode: Boolean by mutableStateOf(false)
        private set

    fun requestMore() {
        // TODO
    }

    fun toggleMultiSelectMode(enabled: Boolean) {
        multiSelectMode = enabled
    }

    private data class ListState(val list: List<Tab>, val isLoading: Boolean, val reachedEnd: Boolean)

    fun interface OnLinkSelect {
        operator fun invoke(linkUrls: Set<String>)
    }
}