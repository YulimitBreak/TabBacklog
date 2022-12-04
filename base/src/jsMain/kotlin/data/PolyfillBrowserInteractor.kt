package data

import browser.tabs.CreateCreateProperties
import browser.tabs.OnAttachedListener
import browser.tabs.OnCreatedListener
import browser.tabs.OnDetachedListener
import browser.tabs.OnMovedListener
import browser.tabs.OnRemovedListener
import browser.tabs.QueryQueryInfo
import browser.tabs.Tab
import browser.windows.QueryOptions
import browser.windows.WindowType
import data.event.TabUpdate
import data.event.WindowUpdate
import kotlinx.browser.window
import kotlinx.coroutines.await
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.merge
import org.w3c.dom.BroadcastChannel

class PolyfillBrowserInteractor : BrowserInteractor {

    override suspend fun getCurrentTab() = browser.tabs.query(QueryQueryInfo {
        active = true
        currentWindow = true
    }).await().first()

    override suspend fun getTabById(id: Int): Tab = browser.tabs.get(id).await()

    override fun openManager() {
        browser.tabs.create(CreateCreateProperties {
            this.url = "manager.html"
        })
    }

    override fun openPage(url: String, active: Boolean) {
        browser.tabs.create(CreateCreateProperties {
            this.url = url
            this.active = active
        })
    }

    override fun openPages(urls: List<String>) {
        val confirm = window.confirm("You're going to open ${urls.size} tabs")
        if (confirm) {
            urls.forEach { openPage(it, false) }
        }
    }

    override fun closeTabs(tabIds: Collection<Int>) {
        val confirm = tabIds.size < 2 || window.confirm("You're going to close ${tabIds.size} tabs")
        if (confirm) {
            browser.tabs.remove(tabIds.toTypedArray())
        }
    }

    private val updateChannel = BroadcastChannel("bookmark_db_update")
    private val localUpdateFlow = MutableSharedFlow<String>()

    override suspend fun sendBookmarkUpdateMessage(url: String) {
        updateChannel.postMessage(url)
        localUpdateFlow.emit(url)
    }

    override fun subscribeToBookmarkUpdates(): Flow<String> =
        merge(
            callbackFlow {
                updateChannel.onmessage = {
                    trySend(it.data as String)
                }
                awaitClose {
                    updateChannel.onmessage = null
                }
            },
            localUpdateFlow
        )

    override fun subscribeToTabUpdates(): Flow<TabUpdate> =
        callbackFlow {
            val onCreatedListener = fun(event: OnCreatedListener) {
                trySend(TabUpdate.Open(event.tab.id ?: return, event.tab.index, event.tab.windowId))
            }
            val onRemovedListener = fun(event: OnRemovedListener) {
                // Check to not handle it extra time
                if (!event.removeInfo.isWindowClosing) {
                    trySend(TabUpdate.Close(event.tabId))
                }
            }
            val onAttachedListener = fun(event: OnAttachedListener) {
                trySend(TabUpdate.Open(event.tabId, event.attachInfo.newPosition, event.attachInfo.newWindowId))
            }
            val onDetachedListener = fun(event: OnDetachedListener) {
                trySend(TabUpdate.Close(event.tabId))
            }
            val onMovedListener = fun(event: OnMovedListener) {
                trySend(TabUpdate.Move(event.tabId, event.moveInfo.toIndex))
            }
            browser.tabs.onCreated.addListener(onCreatedListener)
            browser.tabs.onRemoved.addListener(onRemovedListener)
            browser.tabs.onAttached.addListener(onAttachedListener)
            browser.tabs.onDetached.addListener(onDetachedListener)
            browser.tabs.onMoved.addListener(onMovedListener)
            awaitClose {
                browser.tabs.onCreated.removeListener(onCreatedListener)
                browser.tabs.onRemoved.removeListener(onRemovedListener)
                browser.tabs.onAttached.removeListener(onAttachedListener)
                browser.tabs.onDetached.removeListener(onDetachedListener)
                browser.tabs.onMoved.removeListener(onMovedListener)
            }
        }

    override fun subscribeToWindowUpdates(): Flow<WindowUpdate> = callbackFlow {
        val onCreatedListener = fun(event: browser.windows.OnCreatedListener) {
            trySend(WindowUpdate.Open(event.window.id ?: return))
        }
        val onRemovedListener = fun(event: browser.windows.OnRemovedListener) {
            trySend(WindowUpdate.Close(event.windowId))
        }
        browser.windows.onCreated.addListener(onCreatedListener)
        browser.windows.onRemoved.addListener(onRemovedListener)
        awaitClose {
            browser.windows.onCreated.removeListener(onCreatedListener)
            browser.windows.onRemoved.removeListener(onRemovedListener)
        }
    }

    override suspend fun getWindowIds(): List<Int> =
        browser.windows.getAll(QueryOptions {
            populate = false
        }).await()
            // Filtering instead of using it in query because WindowType mapping is broken
            .filter { WindowType.valueOf(it.type?.toString() ?: return@filter false) == WindowType.normal }
            .mapNotNull { it.id }

    override suspend fun getWindowTabs(windowId: Int): List<Tab> =
        browser.tabs.query(
            QueryQueryInfo {
                this.windowId = windowId
            }
        ).await().toList()

    override suspend fun getCurrentWindowId(): Int? =
        browser.windows.getCurrent().await().id
}