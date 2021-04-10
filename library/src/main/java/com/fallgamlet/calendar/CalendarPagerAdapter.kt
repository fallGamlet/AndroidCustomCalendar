package com.fallgamlet.calendar

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import java.util.*


open class CalendarPagerAdapter(
    config: MonthViewConfig? = null,
    minMonth: YearMonth? = null,
    maxMonth: YearMonth? = null,
    var createPageWrapper: ((parent: ViewGroup, month: YearMonthDay) -> ViewGroup)? = null,
    var onChangeListener: ((monthView: MonthView) -> Unit)? = null,
    onPrevClickListener: (() -> Unit)? = null,
    onNextClickListener: (() -> Unit)? = null,
    onDayClickListener: ((day: YearMonthDay, holder: DayViewHolder) -> Unit)? = null
) : PagerAdapter() {

    private var monthCount = MAX_VALUE
    private var viewContainer: ViewGroup? = null
    private var monthsMap = mutableMapOf<Int, YearMonth>()
    var config: MonthViewConfig
        private set
    lateinit var minMonth: YearMonth
        private set
    lateinit var maxMonth: YearMonth
        private set

    var onPrevClickListener: (() -> Unit)? = onPrevClickListener
        set(value) {
            field = value
            updateListeners()
        }

    var onNextClickListener: (() -> Unit)? = onNextClickListener
        set(value) {
            field = value
            updateListeners()
        }

    var onDayClickListener: ((day: YearMonthDay, holder: DayViewHolder) -> Unit)? = onDayClickListener
        set(value) {
            field = value
            updateListeners()
        }


    init {
        this.config = config ?: MonthViewConfig()
        val start = minMonth ?: Calendar.getInstance()
            .apply { add(Calendar.MONTH, -MAX_VALUE / 2) }
            .toYearMonth()
        val end = maxMonth ?: start.plusMonth(MAX_VALUE)
        setRange(start, end)
    }

    private fun updateListeners() {
        getCachedViews().forEach(::setListenersIntoMonthView)
    }

    private fun setListenersIntoMonthView(monthView: MonthView) {
        val isMinMonth = monthView.currentMonth.monthsBetween(minMonth) == 0
        val isMaxMonth = monthView.currentMonth.monthsBetween(maxMonth) == 0
        monthView.onPrevClickListener = if (isMinMonth) null else onPrevClickListener
        monthView.onNextClickListener = if (isMaxMonth) null else onNextClickListener
        monthView.onChangeListener = onChangeListener
        monthView.onDayClickListener = onDayClickListener
    }

    private fun getCachedViews(): List<MonthView> {
        val views = viewContainer ?: return emptyList()
        return (0 until views.childCount)
            .mapNotNull { i -> views.getChildAt(i) as? MonthView }
    }

    fun setConfig(config: MonthViewConfig) {
        this.config = config
        notifyCalendarChanged()
    }

    fun setRange(minMonth: YearMonth, maxMonth: YearMonth) {
        val isValid = minMonth.monthsBetween(maxMonth) >= 0
        this.minMonth = minMonth
        this.maxMonth = if (isValid) maxMonth else minMonth
        monthCount = minMonth.monthsBetween(maxMonth)
        notifyCalendarChanged()
    }

    fun notifyCalendarChanged() {
        getCachedViews().forEach(MonthView::refresh)
    }

    override fun getCount(): Int = monthCount

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        viewContainer = container
        val month = getMonth(position).toYearMonthDay()
        val view = createMonthView(container.context)
        val viewWrapper = createPageWrapper?.invoke(container, month)
            ?.also { it.addView(view) }
            ?: view
        container.addView(viewWrapper)
        view.setConfig(config)
        view.setMonth(month)
        setListenersIntoMonthView(view)
        return view
    }

    private fun createMonthView(context: Context): MonthView {
        return MonthView(context).also {
            it.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
    }

    private fun getMonth(position: Int): YearMonth {
        return monthsMap.getOrPut(position) { minMonth.plusMonth(position) }
    }

    fun getPositionForMonth(month: YearMonth): Int {
        val position = minMonth.monthsBetween(month)
        return if (position in (0..monthCount)) position else -1
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View?)
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean = (view == `object`)

    companion object {
        const val MAX_VALUE = 500
    }
}
