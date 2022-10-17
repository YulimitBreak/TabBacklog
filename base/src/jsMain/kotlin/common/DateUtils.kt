package common

import kotlinx.datetime.*
import kotlin.math.absoluteValue

object DateUtils {
    val now get() = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    val today get() = now.date

    fun formatTimeRelation(target: LocalDate, pivot: LocalDate = today): String {
        if (target == pivot) return "Today"
        val diff = target - pivot
        val weeks = diff.days / 7
        with(diff) {
            return when {
                years == 1 -> "Next year"
                years > 0 -> "In $years years"
                years == -1 -> "Last year"
                years < 0 -> "${years.absoluteValue} years ago"
                months == 1 -> "Next month"
                months > 0 -> "In $months months"
                months == -1 -> "Last month"
                months < 0 -> "${months.absoluteValue} months ago"
                weeks == 1 -> "Next week"
                weeks > 0 -> "In $weeks weeks"
                weeks == -1 -> "Last week"
                weeks < 0 -> "${weeks.absoluteValue} weeks ago"
                days == 1 -> "Tomorrow"
                days > 0 -> "In $days days"
                days == -1 -> "Yesterday"
                days < 0 -> "${days.absoluteValue} days ago"
                else -> "Today"
            }
        }
    }


    object Formatter {
        fun DmySlash(date: LocalDate) =
            date.dayOfMonth.toString().padStart(2, padChar = '0') + "/" +
                    date.monthNumber.toString().padStart(2, padChar = '0') + "/" +
                    (date.year % 100).toString().padStart(2, padChar = '0')

    }
}