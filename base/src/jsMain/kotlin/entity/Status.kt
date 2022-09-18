package entity

import kotlinx.datetime.LocalDate

sealed class BookmarkType {
    data class Library(
        val rating: Rating?,
    ) : BookmarkType()

    data class Task(
        val deadline: LocalDate?,
    ) : BookmarkType()

    data class Backlog(
        val highPriority: Boolean,
    ) : BookmarkType()

    data class Reminder(
        val remindDate: LocalDate,
    )
}