package entity

data class BrowserTab(
    val tabId: Int,
    val url: String,
    val favIcon: String?,
    val title: String,
    val bookmark: Bookmark? = null
)