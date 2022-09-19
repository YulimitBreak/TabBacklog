package entity

import kotlinx.datetime.LocalDate

sealed class BookmarkStatus(val type: BookmarkType) {
    data class Library(
        val rating: Rating?,
    ) : BookmarkStatus(BookmarkType.LIBRARY)

    data class Task(
        val deadline: LocalDate?,
    ) : BookmarkStatus(BookmarkType.TASK)

    object Backlog : BookmarkStatus(BookmarkType.BACKLOG)

    data class Reminder(
        val remindDate: LocalDate?,
    ) : BookmarkStatus(BookmarkType.REMINDER)
}