package ui.page.tablist

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import common.produce
import common.receive
import data.BookmarkRepository
import data.BrowserInteractor
import entity.BrowserTab
import entity.core.Loadable
import entity.core.load
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import ui.common.delegate.MultiSelectDelegate

class TabListModel(
    private val coroutineScope: CoroutineScope,
    private val browserInteractor: BrowserInteractor,
    private val bookmarkRepository: BookmarkRepository,
    onTabSelectState: State<OnTabSelect>,
) {

    private val onLinkSelect by onTabSelectState

    private var tabsChannel: ReceiveChannel<BrowserTab> = Channel()

    private var listState by mutableStateOf(ListState(emptyList(), isLoading = false, reachedEnd = false))
    val tabs get() = listState.list
    val isLoading get() = listState.isLoading
    val reachedEnd get() = listState.reachedEnd

    private val multiSelectDelegate = MultiSelectDelegate(BrowserTab::tabId)

    val multiSelectMode: Boolean get() = multiSelectDelegate.multiSelectMode
    val selectedTabs get() = multiSelectDelegate.selectedIds

    var selectedWindow: Int? by mutableStateOf(null)
        private set
    var openedWindows by mutableStateOf<Loadable<List<Int>>>(Loadable.Loading())
        private set

    init {
        coroutineScope.load(::openedWindows::set) {
            browserInteractor.getWindowIds()
        }
        coroutineScope.launch {
            selectWindow(browserInteractor.getCurrentWindowId() ?: return@launch)
        }
    }

    fun selectWindow(windowId: Int?) {
        if (selectedWindow == windowId) return
        selectedWindow = windowId
        if (windowId != null) {
            tabsChannel.cancel()
            tabsChannel = readTabs(windowId)
            listState = ListState(emptyList(), isLoading = false, reachedEnd = false)
            requestMore()
        }
    }

    private fun readTabs(windowId: Int): ReceiveChannel<BrowserTab> = flow {
        browserInteractor.getWindowTabs(windowId).forEach { emit(it) }
    }.mapNotNull { tab ->
        val url = tab.url ?: return@mapNotNull null
        BrowserTab(
            tab.id ?: return@mapNotNull null,
            url,
            tab.favIconUrl,
            tab.title ?: "",
            bookmarkRepository.loadBookmark(url)
        )
    }.produce(coroutineScope)

    fun requestMore() {
        coroutineScope.launch {
            listState = listState.copy(isLoading = true)
            val newTabs = tabsChannel.receive(TAB_PAGE_SIZE)
            listState = listState.copy(
                list = (listState.list + newTabs).distinct(),
                isLoading = false,
                reachedEnd = newTabs.size < TAB_PAGE_SIZE
            )
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

    companion object {
        const val TAB_PAGE_SIZE = 10
    }
}