package entity

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

data class Bookmark(
    val url: String,
    val title: String,
    val favicon: String?,
    val status: BookmarkStatus,
    val creationDate: LocalDateTime?,
    val expirationDate: LocalDate? = null,
    val tags: Set<Tag> = emptySet(),
    val comment: String = "",
) {
    val isSaved get() = creationDate != null
}