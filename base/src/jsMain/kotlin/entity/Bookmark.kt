package entity

import kotlinx.datetime.LocalDateTime

data class Bookmark(
    val id: Long,
    val url: String,
    val title: String,
    val creationDate: LocalDateTime,
    val type: BookmarkType,
    val tags: Set<Tag> = emptySet(),
    val comment: String = "",
)