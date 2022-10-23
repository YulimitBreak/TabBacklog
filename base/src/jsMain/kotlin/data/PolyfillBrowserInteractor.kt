package data

import browser.tabs.QueryQueryInfo
import kotlinx.coroutines.await

class PolyfillBrowserInteractor : BrowserInteractor {

    override suspend fun getCurrentTab() = browser.tabs.query(QueryQueryInfo {
        active = true
        currentWindow = true
    }).await().first()
}