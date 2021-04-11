package com.fallgamlet.customcalendar

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import com.fallgamlet.calendar.*
import java.util.*

class CalendarViewPagerExample1Fragment: Fragment(R.layout.example_calendar_view_pager_1) {

    private lateinit var calendarView: CalendarViewPager
    private lateinit var logsView: TextView
    private lateinit var today: YearMonthDay
    private lateinit var defaultMonth: YearMonth
    private var activeDrawable: Drawable? = null
    private var todayDrawable: Drawable? = null
    private var todayActiveDrawable: Drawable? = null
    private var activatedDays = mutableSetOf<YearMonthDay>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        calendarView = view.findViewById(R.id.calendarView)
        logsView = view.findViewById(R.id.logsView)
        val context = view.context
        activeDrawable = ContextCompat.getDrawable(context, R.drawable.shape_circle_activated)
        todayDrawable = ContextCompat.getDrawable(context, R.drawable.shape_dot_bottom)
        todayActiveDrawable = ContextCompat.getDrawable(context, R.drawable.shape_circle_activate_with_dot_bottom)

        today = Calendar.getInstance().toYearMonthDay()
        defaultMonth = today.toYearMonth()
        val minMonth = defaultMonth.plusMonth(-7)
        val maxMonth = defaultMonth.plusMonth(7)

        calendarView.setOnChangeListener(::handleMonthViewChanges)
        calendarView.setOnDayClickListener(::handleOnDayClicked)
        calendarView.pageWrapperCreator = ::createPageWrapView
        calendarView.setRange(minMonth, maxMonth)
    }

    private fun handleMonthViewChanges(monthView: MonthView) {
        pushLog("MonthView changed ${monthView.currentMonth}")
        monthView.monthDayViews
            .forEach { handleMonthDay(it.key, it.value) }
    }

    private fun handleMonthDay(day: YearMonthDay, holder: DayViewHolder) {
        val drawable = getDrawableForDay(day)
        ViewCompat.setBackground(holder.rootView, drawable)
    }

    private fun getDrawableForDay(day: YearMonthDay): Drawable? {
        val isActive = day in activatedDays
        val isToday = day == today
        return when {
            isActive && isToday -> todayActiveDrawable
            isActive -> activeDrawable
            isToday -> todayDrawable
            else -> null
        }
    }

    private fun handleOnDayClicked(day: YearMonthDay, holder: DayViewHolder) {
        pushLog("MonthView pressed on day $day")
        if (!activatedDays.remove(day)) activatedDays.add(day)
        handleMonthDay(day, holder)
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
        if (logsView.text == null) logsView.text = ""
        logsView.text = StringBuilder(logsView.text)
            .apply {
                insert(0, "$text\n")
                if (length > 1000) delete(1000, length)
            }
    }

}
