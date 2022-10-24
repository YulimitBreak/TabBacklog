package data

import browser.tabs.Tab

interface BrowserInteractor {

    suspend fun getCurrentTab(): Tab
    fun openManager()
    fun openBookmark(url: String)
}