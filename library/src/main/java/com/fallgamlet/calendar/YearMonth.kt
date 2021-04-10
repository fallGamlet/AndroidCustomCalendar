package com.fallgamlet.calendar

data class YearMonth(
    val year: Int,
    val month: Int
) {
    fun plusMonth(month: Int): YearMonth {
        val months = totalMonths() + month
        return YearMonth(
            year = months / 12,
            month = months % 12
        )
    }

    fun totalMonths() = MONTHS_IN_YEAR * year + month

    companion object {
        const val MONTHS_IN_YEAR = 12
    }
}
