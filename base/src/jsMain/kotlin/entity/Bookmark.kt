package entity

import kotlinx.datetime.LocalDateTime

data class Bookmark(
    val url: String,
    val title: String,
    val favicon: String?,
    val type: BookmarkType,
    val creationDate: LocalDateTime?,
    val tags: Set<Tag> = emptySet(),
    val comment: String = "",
) {
    val isSaved get() = creationDate != null
}