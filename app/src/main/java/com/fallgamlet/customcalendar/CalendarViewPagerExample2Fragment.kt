package com.fallgamlet.customcalendar

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.fallgamlet.calendar.CalendarViewPager
import com.fallgamlet.calendar.toYearMonth
import java.util.*

class CalendarViewPagerExample2Fragment: Fragment(R.layout.example_calendar_view_pager_2) {

    private lateinit var calendarView: CalendarViewPager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        calendarView = view.findViewById(R.id.calendarView)
        val defaultMonth = Calendar.getInstance().toYearMonth()
        calendarView.setConfig(calendarView.config.copy(
            buttonTintList = colorStateListWithAlphaForPressAndDisabled(calendarView.config.buttonTintList.defaultColor)
        ))
        calendarView.setRange(defaultMonth, defaultMonth)
    }

}
