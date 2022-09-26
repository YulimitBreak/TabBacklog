package entity

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

data class Bookmark(
    val url: String,
    val title: String,
    val favicon: String?,
    val type: BookmarkType,
    val creationDate: LocalDateTime?,
    val deadline: LocalDate? = null,
    val remindDate: LocalDate? = null,
    val expirationDate: LocalDate? = null,
    val tags: Set<String> = emptySet(),
    val favorite: Boolean = false,
    val comment: String = "",
) {
    val isSaved get() = creationDate != null
}