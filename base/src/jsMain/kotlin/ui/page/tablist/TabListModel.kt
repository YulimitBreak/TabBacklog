package ui.page.tablist

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import browser.tabs.Tab
import common.produce
import common.receive
import data.BookmarkRepository
import data.BrowserInteractor
import data.event.TabUpdate
import data.event.WindowUpdate
import entity.BrowserTab
import entity.core.Loadable
import entity.core.load
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.MutableSharedFlow
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

    private val dbUpdateRelay = MutableSharedFlow<String>(extraBufferCapacity = 100)

    init {
        coroutineScope.load(::openedWindows::set) { browserInteractor.getWindowIds() }
        coroutineScope.launch { selectWindow(browserInteractor.getCurrentWindowId() ?: return@launch) }
        coroutineScope.launch { browserInteractor.subscribeToWindowUpdates().collect(::handleWindowUpdate) }
        coroutineScope.launch { browserInteractor.subscribeToTabUpdates().collect(::handleTabUpdate) }
        coroutineScope.launch { browserInteractor.subscribeToBookmarkUpdates().collect(dbUpdateRelay::emit) }
        coroutineScope.launch {
            dbUpdateRelay.collect { url ->
                val bookmark = bookmarkRepository.loadBookmark(url)
                updateList { list -> list.map { if (it.url == url) it.copy(bookmark = bookmark) else it } }
            }
        }
    }

    private fun updateList(update: (List<BrowserTab>) -> List<BrowserTab>) {
        listState = listState.copy(list = update(listState.list))
    }

    private fun handleWindowUpdate(update: WindowUpdate) {
        when (update) {
            is WindowUpdate.Close -> {
                val openedWindows = openedWindows.value ?: return
                val selected = openedWindows.indexOf(selectedWindow).takeIf { it >= 0 }
                    .takeIf { selectedWindow == update.windowId }
                val newOpenedWindows = openedWindows - update.windowId
                this@TabListModel.openedWindows = Loadable.Success(newOpenedWindows)
                if (newOpenedWindows.isEmpty()) return
                if (selected == null) return
                if (selected > 0) {
                    selectWindow(newOpenedWindows[selected - 1])
                } else {
                    selectWindow(newOpenedWindows[0])
                }
            }

            is WindowUpdate.Open -> {
                this@TabListModel.openedWindows = openedWindows.map { it + update.windowId }
            }
        }
    }

    private suspend fun handleTabUpdate(update: TabUpdate) {
        when (update) {
            is TabUpdate.Open -> {
                if (update.windowId != selectedWindow) return
                val tab = browserInteractor.getTabById(update.tabId).toEntity() ?: return
                updateList { list ->
                    list.take(update.index) + tab + list.drop(update.index)
                }
            }

            is TabUpdate.Close -> updateList { list ->
                list.filter { it.tabId != update.tabId }
            }

            is TabUpdate.Move -> {
                val list = listState.list
                val tab = list.find { it.tabId == update.tabId } ?: return
                val listMinusTab = list - tab
                listState = listState.copy(
                    list = listMinusTab.take(update.index) + tab + listMinusTab.drop(update.index)
                )
            }

            is TabUpdate.Update -> {
                val original = listState.list.find { it.tabId == update.tabId }
                val bookmark = original?.bookmark ?: if (update.url != null && update.url != original?.url) {
                    bookmarkRepository.loadBookmark(update.url)
                } else null
                updateList { list ->
                    list.map {
                        if (it.tabId != update.tabId) return@map it

                        it.copy(
                            url = update.url ?: it.url,
                            title = update.title ?: it.title,
                            favIcon = update.favicon ?: it.favIcon,
                            bookmark = bookmark,
                        )
                    }
                }
            }
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

    private suspend fun Tab.toEntity(loadBookmark: Boolean = true): BrowserTab? {
        val url = url ?: return null
        return BrowserTab(
            id ?: return null,
            url,
            favIconUrl,
            title ?: "",
            if (loadBookmark) bookmarkRepository.loadBookmark(url) else null
        )
    }

    private fun readTabs(windowId: Int): ReceiveChannel<BrowserTab> = flow {
        browserInteractor.getWindowTabs(windowId).forEach { emit(it) }
    }.mapNotNull { it.toEntity() }.produce(coroutineScope)

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

    fun closeSelectedTabs() {
        browserInteractor.closeTabs(multiSelectDelegate.selectedIds)
    }

    private data class ListState(val list: List<BrowserTab>, val isLoading: Boolean, val reachedEnd: Boolean)

    fun interface OnTabSelect {
        operator fun invoke(linkUrls: Set<BrowserTab>)
    }

    companion object {
        const val TAB_PAGE_SIZE = 10
    }
}