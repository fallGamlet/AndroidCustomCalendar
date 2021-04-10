package com.fallgamlet.customcalendar

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.fallgamlet.calendar.MonthView
import com.fallgamlet.calendar.YearMonthDay
import com.fallgamlet.calendar.monthsBetween

class MonthViewExample2Fragment: Fragment(R.layout.example_month_view_2) {

    private lateinit var monthView: MonthView
    private lateinit var defaultMonth: YearMonthDay

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        monthView = view.findViewById(R.id.monthView)
        defaultMonth = monthView.currentMonth
        updateButtonsListeners()
    }

    private fun updateButtonsListeners() {
        val diffMonths = defaultMonth.monthsBetween(monthView.currentMonth)
        monthView.onPrevClickListener = if (diffMonths > -8) ::prevMonth else null
        monthView.onNextClickListener = if (diffMonths < 8)::nextMonth else null
    }

    private fun prevMonth() {
        monthView.setMonth(monthView.currentMonth.plusMonth(-1))
        updateButtonsListeners()
    }

    private fun nextMonth() {
        monthView.setMonth(monthView.currentMonth.plusMonth(1))
        updateButtonsListeners()
    }



}
