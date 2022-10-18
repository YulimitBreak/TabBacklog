package ui.common.datepicker

import common.DateUtils
import entity.core.EnglishPlural
import entity.core.PluralText
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus

sealed class RelativeDateTarget(val mode: TimerDatePickerMode) {

    abstract fun resolve(pivot: LocalDate = DateUtils.today): LocalDate?

    object None : RelativeDateTarget(TimerDatePickerMode.NONE) {
        override fun resolve(pivot: LocalDate): LocalDate? = null
    }

    data class SetDate(val date: LocalDate?) : RelativeDateTarget(TimerDatePickerMode.SET) {
        override fun resolve(pivot: LocalDate): LocalDate? = date
    }

    sealed class Counter(
        private val timeUnit: DateTimeUnit.DateBased,
        val timeUnitName: PluralText,
        mode: TimerDatePickerMode,
    ) : RelativeDateTarget(mode) {
        abstract val count: Int

        override fun resolve(pivot: LocalDate) = pivot.plus(count, timeUnit)

        data class Days(override val count: Int) :
            Counter(DateTimeUnit.DAY, EnglishPlural("day", "days"), TimerDatePickerMode.DAYS)

        data class Weeks(override val count: Int) :
            Counter(DateTimeUnit.WEEK, EnglishPlural("week", "weeks"), TimerDatePickerMode.WEEKS)

        data class Months(override val count: Int) :
            Counter(DateTimeUnit.MONTH, EnglishPlural("month", "months"), TimerDatePickerMode.MONTHS)

        data class Years(override val count: Int) :
            Counter(DateTimeUnit.YEAR, EnglishPlural("year", "years"), TimerDatePickerMode.YEARS)
    }
}