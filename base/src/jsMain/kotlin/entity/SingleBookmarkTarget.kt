package entity

sealed interface SingleBookmarkTarget {

    object CurrentTab : SingleBookmarkTarget

    data class Url(val url: String) : SingleBookmarkTarget

    data class SelectedBookmark(val bookmark: Bookmark) : SingleBookmarkTarget
}