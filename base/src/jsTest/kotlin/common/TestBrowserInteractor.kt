package common

import browser.tabs.Tab
import data.BrowserInteractor
import data.event.TabUpdate
import data.event.WindowUpdate
import entity.Bookmark
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow

class TestBrowserInteractor : BrowserInteractor {
    override suspend fun getCurrentTab(): Tab {
        throw IllegalStateException("Unavailable during tester")
    }

    override suspend fun getTabById(id: Int): Tab {
        throw IllegalStateException("Unavailable during tester")
    }

    override fun openManager() {
        console.log("Request to open manager")
    }

    override fun openPage(url: String, active: Boolean) {
        console.log("Request to open page $url")
    }

    override fun openPages(urls: List<String>) {
        console.log("Request to open pages $urls")
    }

    override fun closeTabs(tabIds: Collection<Int>) {
        console.log("Request to close tabs $tabIds")
    }

    private val updateFlow = MutableSharedFlow<String>()

    override suspend fun sendBookmarkUpdateMessage(url: String) {
        updateFlow.emit(url)
    }

    override fun subscribeToBookmarkUpdates(): Flow<String> = updateFlow

    override fun subscribeToTabUpdates(): Flow<TabUpdate> = flow {}

    override fun subscribeToWindowUpdates(): Flow<WindowUpdate> = flow {}

    override suspend fun getWindowIds(): List<Int> {
        return emptyList()
    }

    override suspend fun getWindowTabs(windowId: Int): List<Tab> {
        return emptyList()
    }

    override suspend fun getCurrentWindowId(): Int? {
        return null
    }

    override suspend fun exportBookmarks(bookmarks: List<Bookmark>) {
    }
}