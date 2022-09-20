package entity

import kotlinx.datetime.LocalDate

data class EditedBookmark(
    val base: Bookmark,
    val title: String = base.title,
    val currentType: BookmarkType = base.type,
    val taskDeadline: LocalDate? = base.deadline,
    val remindDate: LocalDate? = base.remindDate,
    val expirationDate: LocalDate? = base.expirationDate,
    val tags: String = base.tags.joinToString(separator = " ") { "#it" },
    val comment: String = base.comment,
) {
    val isNew get() = false // !base.isSaved
}