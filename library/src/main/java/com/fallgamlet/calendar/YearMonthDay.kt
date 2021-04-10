package com.fallgamlet.calendar

data class YearMonthDay(
    val year: Int,
    val month: Int,
    val day: Int = -1
) {
    fun plusMonth(month: Int): YearMonthDay {
        val months = totalMonths() + month
        return YearMonthDay(
            year = months / 12,
            month = months % 12,
            day = day
        )
    }

    fun totalMonths() = YearMonth.MONTHS_IN_YEAR * year + month

    fun isYearMonthEquals(other: YearMonthDay): Boolean =
        year == other.year && month == other.month
}
