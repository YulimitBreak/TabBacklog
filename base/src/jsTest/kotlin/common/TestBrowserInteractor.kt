package common

import browser.tabs.Tab
import data.BrowserInteractor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

class TestBrowserInteractor : BrowserInteractor {
    override suspend fun getCurrentTab(): Tab {
        throw IllegalStateException("Unavailable during tester")
    }

    override fun openManager() {
        console.log("Request to open manager")
    }

    override fun openPage(url: String) {
        console.log("Request to open page $url")
    }

    override fun openPages(urls: List<String>) {
        console.log("Request to open pages $urls")
    }

    private val updateFlow = MutableSharedFlow<String>()

    override suspend fun sendUpdateMessage(url: String) {
        updateFlow.emit(url)
    }

    override fun subscribeToDbUpdates(): Flow<String> = updateFlow


    override suspend fun getWindowIds(): List<Int> {
        return emptyList()
    }

    override suspend fun getWindowTabs(windowId: Int): List<Tab> {
        return emptyList()
    }

    override suspend fun getCurrentWindowId(): Int? {
        return null
    }
}