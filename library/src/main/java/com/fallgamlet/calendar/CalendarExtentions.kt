package com.fallgamlet.calendar

import java.util.*

fun Calendar.toYearMonthDay() =
    YearMonthDay(get(Calendar.YEAR), get(Calendar.MONTH), get(Calendar.DAY_OF_MONTH))

fun Calendar.toYearMonth() =
    YearMonth(get(Calendar.YEAR), get(Calendar.MONTH))

fun YearMonthDay.toCalendar(firstDayOfWeek: Int? = null) = Calendar.getInstance().also {
    val isCorrectDay = day in (it.getActualMinimum(Calendar.DAY_OF_MONTH)..it.getActualMaximum(Calendar.DAY_OF_MONTH))
    it.set(Calendar.YEAR, year)
    it.set(Calendar.MONTH, month)
    if (isCorrectDay) it.set(Calendar.DAY_OF_MONTH, day)
    else it.set(Calendar.DAY_OF_MONTH, it.getActualMinimum(Calendar.DAY_OF_MONTH))
    it.clear(Calendar.HOUR_OF_DAY)
    it.clear(Calendar.MINUTE)
    it.clear(Calendar.MILLISECOND)
    if (firstDayOfWeek != null) it.firstDayOfWeek = firstDayOfWeek
}

fun YearMonthDay.toYearMonth() = YearMonth(year, month)

fun YearMonth.toYearMonthDay() = YearMonthDay(year, month)

fun YearMonthDay.monthsBetween(other: YearMonth) =
    monthsBetween(year, month, other.year, other.month)

fun YearMonthDay.monthsBetween(other: YearMonthDay) =
    monthsBetween(year, month, other.year, other.month)

fun YearMonth.monthsBetween(other: YearMonth) =
    monthsBetween(year, month, other.year, other.month)

internal fun monthsBetween(fromYear: Int, fromMonth: Int, toYear: Int, toMonth: Int): Int =
    12 * (toYear - fromYear) + toMonth - fromMonth
