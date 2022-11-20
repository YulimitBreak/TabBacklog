package entity

sealed interface SingleBookmarkSource {

    object CurrentTab : SingleBookmarkSource

    data class Url(val url: String) : SingleBookmarkSource

    data class SelectedBookmark(val bookmark: Bookmark) : SingleBookmarkSource
}