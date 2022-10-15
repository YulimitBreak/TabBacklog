package common

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

object DateUtils {
    val now get() = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    val today get() = now.date
}