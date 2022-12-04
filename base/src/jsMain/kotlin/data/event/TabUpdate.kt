package data.event

sealed interface TabUpdate {
    data class Open(val tabId: Int, val index: Int, val windowId: Int) : TabUpdate
    data class Close(val tabId: Int) : TabUpdate
    data class Move(val tabId: Int, val index: Int) : TabUpdate
}