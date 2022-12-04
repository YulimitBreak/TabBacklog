package data

import androidx.compose.runtime.compositionLocalOf
import browser.tabs.Tab
import data.event.TabUpdate
import data.event.WindowUpdate
import entity.error.CompositionLocalError
import kotlinx.coroutines.flow.Flow

interface BrowserInteractor {

    suspend fun getCurrentTab(): Tab

    suspend fun getTabById(id: Int): Tab

    fun openManager()

    fun openPage(url: String, active: Boolean = true)

    fun openPages(urls: List<String>)

    fun closeTabs(tabIds: Collection<Int>)

    suspend fun sendBookmarkUpdateMessage(url: String)

    fun subscribeToBookmarkUpdates(): Flow<String>

    fun subscribeToTabUpdates(): Flow<TabUpdate>

    fun subscribeToWindowUpdates(): Flow<WindowUpdate>

    suspend fun getWindowIds(): List<Int>

    suspend fun getWindowTabs(windowId: Int): List<Tab>

    suspend fun getCurrentWindowId(): Int?

    companion object {
        val Local = compositionLocalOf<BrowserInteractor> {
            throw CompositionLocalError("BrowserInteractor")
        }
    }
}