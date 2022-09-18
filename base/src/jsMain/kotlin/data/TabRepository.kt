package data

import browser.tabs.CreateCreateProperties
import browser.tabs.HighlightHighlightInfo
import browser.tabs.QueryQueryInfo
import kotlinx.coroutines.await

class TabRepository {

    suspend fun openOrSwitch(url: String) {
        val tab = browser.tabs.query(QueryQueryInfo {
            this.url = url
            currentWindow = true
        }).await().singleOrNull()
        if (tab != null) {
            browser.tabs.highlight(
                HighlightHighlightInfo {
                    this.tabs = tab.index
                }
            )?.await()
        } else {
            browser.tabs.create(CreateCreateProperties {
                this.url = url
            })
        }
    }

    suspend fun openManager() {
        openOrSwitch("manager.html")
    }
}