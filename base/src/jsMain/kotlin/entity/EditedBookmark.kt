package entity

import common.safeCast
import kotlinx.datetime.LocalDate

data class EditedBookmark(
    val base: Bookmark,
    val title: String = base.title,
    val currentType: BookmarkType? = base.status.type,
    val libraryRating: Rating? = currentType?.safeCast<BookmarkStatus.Library>()?.rating,
    val taskDeadline: LocalDate? = currentType?.safeCast<BookmarkStatus.Task>()?.deadline,
    val remindDate: LocalDate? = currentType?.safeCast<BookmarkStatus.Reminder>()?.remindDate,
    val expirationDate: LocalDate? = base.expirationDate,
    val tags: String = base.tags.joinToString(separator = " ") { "#it" },
    val comment: String = base.comment,
) {
    val isNew get() = false // !base.isSaved

    val isBeingDeleted get() = currentType == null
}