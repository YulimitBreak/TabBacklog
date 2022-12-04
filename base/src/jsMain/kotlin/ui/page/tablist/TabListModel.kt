package ui.page.tablist

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import data.BookmarkRepository
import data.BrowserInteractor
import entity.BrowserTab
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

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
        // TODO pagination
        coroutineScope.launch {
            listState = listState.copy(isLoading = true)
            browserInteractor.getWindowTabs(browserInteractor.getCurrentWindowId() ?: return@launch)
                .mapNotNull { tab ->
                    val url = tab.url ?: return@mapNotNull null
                    BrowserTab(
                        tab.id ?: return@mapNotNull null,
                        url,
                        tab.favIconUrl,
                        tab.title ?: "",
                        bookmarkRepository.loadBookmark(url)
                    )
                }.let { tabs ->
                    listState = listState.copy(list = tabs, isLoading = false, reachedEnd = true)
                }
        }
    }

    fun toggleMultiSelectMode(enabled: Boolean) {
        multiSelectMode = enabled
    }

    fun selectTab(tab: BrowserTab, ctrlKey: Boolean, shiftKey: Boolean) {
        // TODO multiselect
        selectedTabs = setOf(tab.tabId)
        onLinkSelect(setOf(tab.url))
    }

    private data class ListState(val list: List<BrowserTab>, val isLoading: Boolean, val reachedEnd: Boolean)

    fun interface OnLinkSelect {
        operator fun invoke(linkUrls: Set<String>)
    }
}