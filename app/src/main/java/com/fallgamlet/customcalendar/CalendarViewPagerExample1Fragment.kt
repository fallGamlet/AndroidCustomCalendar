package com.fallgamlet.customcalendar

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.fallgamlet.calendar.*
import java.util.*

class CalendarViewPagerExample1Fragment: Fragment(R.layout.example_calendar_view_pager_1) {

    private lateinit var calendarView: CalendarViewPager
    private lateinit var logsView: TextView
    private lateinit var today: YearMonthDay
    private lateinit var defaultMonth: YearMonth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        calendarView = view.findViewById(R.id.calendarView)
        logsView = view.findViewById(R.id.logsView)

        today = Calendar.getInstance().toYearMonthDay()
        defaultMonth = today.toYearMonth()
        val minMonth = defaultMonth.plusMonth(-7)
        val maxMonth = defaultMonth.plusMonth(7)

        calendarView.pageWrapperCreator = ::createPageWrapView
        calendarView.setRange(minMonth, maxMonth)

    }

    private fun createPageWrapView(parent: ViewGroup, month: YearMonth): WrapperViewHolder {
        return if (month.month % 4 < 2) createCardWrapper(parent, month)
        else createSimpleFrameWrapper(parent, month)
    }

    private fun createCardWrapper(parent: ViewGroup, month: YearMonth): WrapperViewHolder {
        val view = layoutInflater.inflate(R.layout.page_wrapper_cards, parent, false)
        return WrapperViewHolder(
            root = view,
            contentView = view.findViewById(R.id.wrapperContentView)
        )
    }

    private fun createSimpleFrameWrapper(parent: ViewGroup, month: YearMonth): WrapperViewHolder {
        val view = layoutInflater.inflate(R.layout.page_wrapper_frame, parent, false) as ViewGroup
        return WrapperViewHolder(view)
    }

    private fun pushLog(text: String?) {
        if (text.isNullOrBlank()) return
        logsView.editableText?.insert(0, text)
    }

}
