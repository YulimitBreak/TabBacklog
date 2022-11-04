package entity.sort

import common.DateUtils
import common.isAfterToday
import entity.Bookmark
import entity.BookmarkType
import entity.retrieve.RetrieveRequest
import kotlinx.datetime.*
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

sealed class SortType : BookmarkSort() {

    data class CreationDate(
        private val unsavedFirst: Boolean = true,
        override val next: BookmarkSort? = null,
        override val isReversed: Boolean = false,
    ) : SortType() {
        override fun isLess(first: Bookmark, second: Bookmark): Boolean = when {
            first.creationDate != null && second.creationDate != null -> first.creationDate < second.creationDate
            first.creationDate != null -> !unsavedFirst
            second.creationDate != null -> unsavedFirst
            else -> false
        }


        override val retrieve = retrieveRequest {
            if (unsavedFirst) {
                join(
                    next?.retrieve?.filter { it.creationDate == null },
                    fetch().select(BookmarkRetrieveQuery.CreationDate(ascending = !isReversed))
                )
            } else {
                join(
                    fetch().select(BookmarkRetrieveQuery.CreationDate(ascending = !isReversed)),
                    next?.retrieve?.filter { it.creationDate == null },
                )
            }
        }

        override fun isEqual(first: Bookmark, second: Bookmark): Boolean = first.creationDate == second.creationDate
    }

    data class Alphabetically(
        override val isReversed: Boolean = false,
    ) : SortType() {
        override fun isLess(first: Bookmark, second: Bookmark): Boolean = first.title < second.title

        override fun isEqual(first: Bookmark, second: Bookmark): Boolean = first.title == second.title

        override val retrieve = RetrieveRequest<_, BookmarkRetrieveQuery> {
            fetch().select(BookmarkRetrieveQuery.Title(ascending = !isReversed))
        }
    }

    data class FavoriteFirst(
        override val next: BookmarkSort? = null,
    ) : SortType() {
        override fun isLess(first: Bookmark, second: Bookmark): Boolean = first.favorite && !second.favorite

        override fun isEqual(first: Bookmark, second: Bookmark): Boolean = first.favorite == second.favorite

        override val retrieve = retrieveRequest {
            join(
                (next?.retrieve ?: fetch()).select(BookmarkRetrieveQuery.Favorite(target = true)),
                (next?.retrieve ?: fetch()).select(BookmarkRetrieveQuery.Favorite(target = false)),
            )
        }
    }

    data class BacklogFirst(
        override val next: BookmarkSort? = null,
    ) : SortType() {
        override fun isLess(first: Bookmark, second: Bookmark): Boolean =
            first.type == BookmarkType.BACKLOG && second.type == BookmarkType.LIBRARY

        override fun isEqual(first: Bookmark, second: Bookmark): Boolean =
            first.type == second.type

        override val retrieve = retrieveRequest {
            join(
                (next?.retrieve ?: fetch()).select(BookmarkRetrieveQuery.Type(target = BookmarkType.BACKLOG)),
                (next?.retrieve ?: fetch()).select(BookmarkRetrieveQuery.Type(target = BookmarkType.LIBRARY)),
            )
        }
    }

    data class LibraryFirst(
        override val next: BookmarkSort? = null,
    ) : SortType() {
        override fun isLess(first: Bookmark, second: Bookmark): Boolean =
            first.type == BookmarkType.LIBRARY && second.type == BookmarkType.BACKLOG

        override fun isEqual(first: Bookmark, second: Bookmark): Boolean =
            first.type == second.type

        override val retrieve = retrieveRequest {
            join(
                (next?.retrieve ?: fetch()).select(BookmarkRetrieveQuery.Type(target = BookmarkType.LIBRARY)),
                (next?.retrieve ?: fetch()).select(BookmarkRetrieveQuery.Type(target = BookmarkType.BACKLOG)),
            )
        }
    }


    data class UnreachedReminderLast(
        override val next: BookmarkSort? = null,
    ) : SortType() {


        private fun isLess(first: LocalDate?, second: LocalDate?): Boolean = when {
            first != null && second != null -> when {
                first.isAfterToday() && second.isAfterToday() -> first < second
                first.isAfterToday() -> false
                second.isAfterToday() -> true
                else -> false // if both are unreached leave sorting of them for later
            }

            first != null -> false // never less than null
            second != null -> second.isAfterToday() //if second is unreached, true, else they are equal
            else -> false
        }

        private fun isEqual(first: LocalDate?, second: LocalDate?) = when {
            first != null && second != null -> when {
                first.isAfterToday() && second.isAfterToday() -> first == second
                // if one is reached and other is unreached they are not equal
                first.isAfterToday() || second.isAfterToday() -> false
                else -> true // if neither is reached they are considered equal
            }

            first != null -> !first.isAfterToday() // null equals reached reminders
            second != null -> !second.isAfterToday()
            else -> true // null equals null
        }

        override fun isLess(first: Bookmark, second: Bookmark): Boolean = isLess(first.remindDate, second.remindDate)
        override fun isEqual(first: Bookmark, second: Bookmark): Boolean = isEqual(first.remindDate, second.remindDate)

        override val retrieve = retrieveRequest {
            join(
                (next?.retrieve ?: fetch()).filter { it.remindDate == null || !it.remindDate.isAfterToday() },
                fetch().select(BookmarkRetrieveQuery.RemindDate(from = DateUtils.today + DatePeriod(days = 1))),
            )
        }
    }

    data class DeadlineFirst(
        override val next: BookmarkSort? = null,
    ) : SortType() {
        override fun isLess(first: Bookmark, second: Bookmark): Boolean =
            when {
                first.deadline == null -> false // null deadline is always closer to the end
                second.deadline == null -> true // bookmark with deadline is earlier than bookmark without deadline
                else -> first.deadline < second.deadline
            }

        override fun isEqual(first: Bookmark, second: Bookmark): Boolean = first.deadline == second.deadline

        override val retrieve = retrieveRequest {
            join(
                fetch().select(BookmarkRetrieveQuery.Deadline()),
                (next?.retrieve ?: fetch()).filter { it.deadline == null }
            )
        }
    }

    data class ReminderFirst(
        override val next: BookmarkSort? = null,
    ) : SortType() {


        private fun isLess(first: LocalDate?, second: LocalDate?): Boolean = when {
            first != null && second != null -> first < second
            first != null && second == null -> !first.isAfterToday() // if first is reached, it goes before null
            first == null && second != null -> second.isAfterToday() // if second is not reached it goes after null
            else -> false // null equals null
        }

        override fun isLess(first: Bookmark, second: Bookmark): Boolean = isLess(first.remindDate, second.remindDate)

        override fun isEqual(first: Bookmark, second: Bookmark): Boolean = first.remindDate == second.remindDate

        override val retrieve = retrieveRequest {
            join(
                fetch().select(BookmarkRetrieveQuery.RemindDate(to = DateUtils.today)),
                (next?.retrieve ?: fetch()).filter { it.remindDate == null },
                fetch().select(BookmarkRetrieveQuery.RemindDate(from = DateUtils.today + DatePeriod(days = 1))),
            )
        }
    }

    data class ExpiringSoonFirst(
        override val next: BookmarkSort? = null,
    ) : SortType() {


        @OptIn(ExperimentalContracts::class)
        private fun expiresSoon(date: LocalDate?): Boolean {
            contract {
                returns(true) implies (date != null)
            }
            return date?.minus(1, DateTimeUnit.WEEK)?.isAfterToday()?.not() ?: false
        }

        override fun isLess(first: Bookmark, second: Bookmark): Boolean = when {
            expiresSoon(first.expirationDate) && expiresSoon(second.expirationDate) -> first.expirationDate < second.expirationDate
            expiresSoon(first.expirationDate) -> true
            else -> false
        }


        override fun isEqual(first: Bookmark, second: Bookmark): Boolean {
            if (!expiresSoon(first.expirationDate) && !expiresSoon(second.expirationDate)) return true
            return first.expirationDate == second.expirationDate
        }

        override val retrieve = retrieveRequest {
            join(
                fetch().select(BookmarkRetrieveQuery.ExpirationDate(to = DateUtils.today.plus(1, DateTimeUnit.WEEK))),
                (next?.retrieve ?: fetch()).filter {
                    it.expirationDate == null || it.expirationDate.minus(
                        1,
                        DateTimeUnit.WEEK
                    ).isAfterToday()
                }
            )
        }
    }

}