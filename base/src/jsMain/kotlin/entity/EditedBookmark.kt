package entity

import common.DateUtils
import kotlinx.datetime.LocalDate

data class EditedBookmark(
    val base: Bookmark,
    val title: String = base.title,
    val currentType: BookmarkType = base.type,
    val deadline: LocalDate? = base.deadline,
    val reminder: LocalDate? = base.remindDate,
    val expiration: LocalDate? = base.expirationDate,
    val tags: List<String> = base.tags.toList(),
    val favorite: Boolean = base.favorite,
    val comment: String = base.comment,
) {
    val isNew get() = !base.isSaved

    fun toImmutableBookmark() = base.copy(
        title = title,
        type = currentType,
        deadline = deadline,
        remindDate = reminder,
        expirationDate = expiration,
        tags = tags.toSet(),
        favorite = favorite,
        comment = comment,
        creationDate = DateUtils.now,
    )
}