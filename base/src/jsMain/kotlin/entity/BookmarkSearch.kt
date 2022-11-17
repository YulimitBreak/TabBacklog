package entity

data class BookmarkSearchConfig(
    val searchString: String = "",
    val tags: Set<String> = emptySet(),
)