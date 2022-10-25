package common

import kotlinx.datetime.LocalDate

fun LocalDate.isBefore(other: LocalDate) = this.toEpochDays() < other.toEpochDays()

fun LocalDate.isAfter(other: LocalDate) = this.toEpochDays() > other.toEpochDays()

fun LocalDate.isBeforeToday() = isBefore(DateUtils.today)

fun LocalDate.isAfterToday() = isAfter(DateUtils.today)