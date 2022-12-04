package entity

sealed interface BookmarkSource {

    object CurrentTab : BookmarkSource

    data class Url(val url: String) : BookmarkSource

    data class SelectedBookmark(val bookmark: Bookmark) : BookmarkSource

    data class Tab(val browserTab: BrowserTab) : BookmarkSource
}