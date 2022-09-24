package entity

import kotlinx.datetime.LocalDate

data class EditedBookmark(
    val base: Bookmark,
    val title: String = base.title,
    val currentType: BookmarkType = base.type,
    val deadline: LocalDate? = base.deadline,
    val reminder: LocalDate? = base.remindDate,
    val expiration: LocalDate? = base.expirationDate,
    val tags: String = base.tags.joinToString(separator = " ") { "#it" },
    val comment: String = base.comment,
) {
    val isNew get() = false // !base.isSaved
}