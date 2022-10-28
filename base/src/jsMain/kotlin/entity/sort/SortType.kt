package entity.sort

import common.isAfterToday
import entity.Bookmark
import entity.BookmarkType
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

sealed class SortType : BookmarkSort() {

    class CreationDate(
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


        override fun isEqual(first: Bookmark, second: Bookmark): Boolean = first.creationDate == second.creationDate
    }

    class Alphabetically(
        override val next: BookmarkSort? = null,
        override val isReversed: Boolean = false,
    ) : SortType() {
        override fun isLess(first: Bookmark, second: Bookmark): Boolean = first.title < second.title

        override fun isEqual(first: Bookmark, second: Bookmark): Boolean = first.title == second.title
    }

    class FavoriteFirst(
        override val next: BookmarkSort? = null,
        override val isReversed: Boolean = false,
    ) : SortType() {
        override fun isLess(first: Bookmark, second: Bookmark): Boolean = first.favorite && !second.favorite

        override fun isEqual(first: Bookmark, second: Bookmark): Boolean = first.favorite == second.favorite
    }

    class BacklogFirst(
        override val next: BookmarkSort? = null,
        override val isReversed: Boolean = false,
    ) : SortType() {
        override fun isLess(first: Bookmark, second: Bookmark): Boolean =
            first.type == BookmarkType.BACKLOG && second.type == BookmarkType.LIBRARY

        override fun isEqual(first: Bookmark, second: Bookmark): Boolean =
            first.type == second.type
    }

    class LibraryFirst(
        override val next: BookmarkSort? = null,
        override val isReversed: Boolean = false,
    ) : SortType() {
        override fun isLess(first: Bookmark, second: Bookmark): Boolean =
            first.type == BookmarkType.LIBRARY && second.type == BookmarkType.BACKLOG

        override fun isEqual(first: Bookmark, second: Bookmark): Boolean =
            first.type == second.type
    }


    class UnreachedReminderLast(
        override val next: BookmarkSort? = null,
        override val isReversed: Boolean = false,
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
    }

    class DeadlineFirst(
        override val next: BookmarkSort? = null,
        override val isReversed: Boolean = false,
    ) : SortType() {
        override fun isLess(first: Bookmark, second: Bookmark): Boolean =
            when {
                first.deadline == null -> false // null deadline is always closer to the end
                second.deadline == null -> true // bookmark with deadline is earlier than bookmark without deadline
                else -> first.deadline < second.deadline
            }

        override fun isEqual(first: Bookmark, second: Bookmark): Boolean = first.deadline == second.deadline
    }

    class ReminderFirst(
        override val next: BookmarkSort? = null,
        override val isReversed: Boolean = false,
    ) : SortType() {


        private fun isLess(first: LocalDate?, second: LocalDate?): Boolean = when {
            first != null && second != null -> first < second
            first != null && second == null -> !first.isAfterToday() // if first is reached, it goes before null
            first == null && second != null -> second.isAfterToday() // if second is not reached it goes after null
            else -> false // null equals null
        }

        override fun isLess(first: Bookmark, second: Bookmark): Boolean = isLess(first.remindDate, second.remindDate)

        override fun isEqual(first: Bookmark, second: Bookmark): Boolean = first.remindDate == second.remindDate
    }

    class ExpiringSoonFirst(
        override val next: BookmarkSort? = null,
        override val isReversed: Boolean = false,
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
    }

}