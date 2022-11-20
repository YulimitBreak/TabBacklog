package entity

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

data class Bookmark(
    val url: String,
    val title: String,
    val favicon: String?,
    val type: BookmarkType,
    val creationDate: LocalDateTime?,
    val remindDate: LocalDate? = null,
    val deadline: LocalDate? = null,
    val expirationDate: LocalDate? = null,
    val tags: List<String> = emptyList(),
    val favorite: Boolean = false,
    val comment: String = "",
) {
    val isSaved get() = creationDate != null
    val hasTimers get() = deadline != null || remindDate != null || expirationDate != null

    fun containsSearch(searchString: String?): Boolean =
        searchString.isNullOrBlank() ||
                title.contains(searchString, ignoreCase = true) ||
                comment.contains(searchString, ignoreCase = true) ||
                url.contains(searchString, ignoreCase = true)
}