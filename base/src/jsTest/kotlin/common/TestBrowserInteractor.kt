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

    private val updateFlow = MutableSharedFlow<String>()

    override suspend fun sendUpdateMessage(url: String) {
        updateFlow.emit(url)
    }

    override fun subscribeToDbUpdates(): Flow<String> = updateFlow
}