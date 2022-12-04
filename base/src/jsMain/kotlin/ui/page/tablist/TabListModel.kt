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
import ui.common.delegate.MultiSelectDelegate

class TabListModel(
    private val coroutineScope: CoroutineScope,
    private val browserInteractor: BrowserInteractor,
    private val bookmarkRepository: BookmarkRepository,
    onTabSelectState: State<OnTabSelect>,
) {

    private val onLinkSelect by onTabSelectState

    private var listState by mutableStateOf(ListState(emptyList(), isLoading = false, reachedEnd = false))
    val tabs get() = listState.list
    val isLoading get() = listState.isLoading
    val reachedEnd get() = listState.reachedEnd

    private val multiSelectDelegate = MultiSelectDelegate(BrowserTab::tabId)

    val multiSelectMode: Boolean get() = multiSelectDelegate.multiSelectMode
    val selectedTabs get() = multiSelectDelegate.selectedIds

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
        multiSelectDelegate.multiSelectMode = enabled
    }

    fun selectTab(tab: BrowserTab, ctrlKey: Boolean, shiftKey: Boolean) {
        multiSelectDelegate.selectItem(listState.list, tab, ctrlKey, shiftKey)
        onLinkSelect(multiSelectDelegate.selectedItems)
    }

    private data class ListState(val list: List<BrowserTab>, val isLoading: Boolean, val reachedEnd: Boolean)

    fun interface OnTabSelect {
        operator fun invoke(linkUrls: Set<BrowserTab>)
    }
}