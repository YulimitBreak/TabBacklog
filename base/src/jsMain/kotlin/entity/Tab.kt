package entity

data class Tab(
    val tabId: Int,
    val url: String,
    val favicon: String?,
    val title: String,
    val bookmark: Bookmark? = null
)