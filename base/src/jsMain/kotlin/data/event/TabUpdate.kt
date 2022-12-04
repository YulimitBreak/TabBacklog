package data.event

sealed interface TabUpdate {
    data class Open(val tabId: Int, val index: Int, val windowId: Int) : TabUpdate
    data class Close(val tabId: Int) : TabUpdate
    data class Move(val tabId: Int, val index: Int) : TabUpdate
    data class Update(val tabId: Int, val title: String?, val favicon: String?, val url: String?) : TabUpdate
}