package ui.common.datepicker

import common.DateUtils
import entity.core.EnglishPlural
import entity.core.PluralText
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus

sealed class DatePickerTarget(val mode: DatePickerMode) {

    abstract fun resolve(pivot: LocalDate = DateUtils.today): LocalDate?

    object None : DatePickerTarget(DatePickerMode.NONE) {
        override fun resolve(pivot: LocalDate): LocalDate? = null
    }

    data class SetDate(val date: LocalDate?) : DatePickerTarget(DatePickerMode.SET) {
        override fun resolve(pivot: LocalDate): LocalDate? = date
    }

    sealed class Counter(
        private val timeUnit: DateTimeUnit.DateBased,
        val timeUnitName: PluralText,
        mode: DatePickerMode,
    ) : DatePickerTarget(mode) {
        abstract val count: Int

        override fun resolve(pivot: LocalDate) = pivot.plus(count, timeUnit)

        data class Days(override val count: Int) :
            Counter(DateTimeUnit.DAY, EnglishPlural("day", "days"), DatePickerMode.DAYS)

        data class Weeks(override val count: Int) :
            Counter(DateTimeUnit.WEEK, EnglishPlural("week", "weeks"), DatePickerMode.WEEKS)

        data class Months(override val count: Int) :
            Counter(DateTimeUnit.MONTH, EnglishPlural("month", "months"), DatePickerMode.MONTHS)

        data class Years(override val count: Int) :
            Counter(DateTimeUnit.YEAR, EnglishPlural("year", "years"), DatePickerMode.YEARS)
    }
}