package data

import browser.tabs.CreateCreateProperties
import browser.tabs.QueryQueryInfo
import kotlinx.coroutines.await

class PolyfillBrowserInteractor : BrowserInteractor {

    override suspend fun getCurrentTab() = browser.tabs.query(QueryQueryInfo {
        active = true
        currentWindow = true
    }).await().first()

    override fun openManager() {
        browser.tabs.create(CreateCreateProperties {
            this.url = "manager.html"
        })
    }
}