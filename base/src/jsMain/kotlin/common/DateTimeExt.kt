package common

import kotlinx.datetime.LocalDate

fun LocalDate.isBeforeToday() = this < DateUtils.today

fun LocalDate.isAfterToday() = this > DateUtils.today