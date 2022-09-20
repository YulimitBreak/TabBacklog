package common

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

object DateUtils {
    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
}