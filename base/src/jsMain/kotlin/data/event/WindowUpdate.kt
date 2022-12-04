package data.event

sealed interface WindowUpdate {
    data class Open(val windowId: Int) : WindowUpdate
    data class Close(val windowId: Int) : WindowUpdate
}