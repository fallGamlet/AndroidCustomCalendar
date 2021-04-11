package com.fallgamlet.calendar

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import java.util.*
import kotlin.math.abs

open class CalendarViewPager : ViewPager {

    var today: YearMonthDay = Calendar.getInstance().toYearMonthDay()
    var config = MonthViewConfig()
        private set

    constructor(context: Context): this(context, null)

    constructor(context: Context, attrs: AttributeSet?): super(context, attrs)  {
        initAttrs(attrs)
    }

    private fun initAttrs(attrs: AttributeSet?) {
        val calendar = Calendar.getInstance()
        today = calendar.toYearMonthDay()
        var config = MonthViewConfigHelper.defaultConfig(calendar, resources)
        attrs?.apply {
            val typedArray = context.obtainStyledAttributes(this, R.styleable.CalendarViewPager)
            config = config.applyAttributes(typedArray)
            typedArray.recycle()
        }
        this.config = config

        this.adapter = CalendarPagerAdapter(
            config = config,
            onPrevClickListener = { setCurrentItemPrev() },
            onNextClickListener = { setCurrentItemNext() }
        )
        setCurrentItem(today.toYearMonth(), false)
    }

    private fun MonthViewConfig.applyAttributes(typedArray: TypedArray): MonthViewConfig {
        return copy(
            weekFirstDay = typedArray.getInt(R.styleable.CalendarViewPager_android_firstDayOfWeek, weekFirstDay),
            monthTextIsVisible = typedArray.getBoolean(R.styleable.CalendarViewPager_cc_monthTextIsVisible, monthTextIsVisible),
            monthButtonIsVisible = typedArray.getBoolean(R.styleable.CalendarViewPager_cc_monthButtonIsVisible, monthButtonIsVisible),
            monthTextAppearance = typedArray.getResourceId(R.styleable.CalendarViewPager_cc_monthTextAppearance, android.R.style.TextAppearance_Material_Title),
            weekDayTextAppearance = typedArray.getResourceId(R.styleable.CalendarViewPager_cc_weekDayTextAppearance, android.R.style.TextAppearance_Material_Body1),
            monthDayTextAppearance = typedArray.getResourceId(R.styleable.CalendarViewPager_cc_monthDayTextAppearance, android.R.style.TextAppearance_Material_Body1),
            monthDayOtherTextAppearance = typedArray.getResourceId(R.styleable.CalendarViewPager_cc_monthDayOtherTextAppearance, android.R.style.TextAppearance_Material_Body1),
            monthTextTint = typedArray.getColorStateList(R.styleable.CalendarViewPager_cc_monthTextTint)
                ?: typedArray.getColor(R.styleable.CalendarViewPager_cc_monthTextTint, monthTextTint.defaultColor)
                    .let { ColorStateList.valueOf(it) },
            buttonTintList = typedArray.getColorStateList(R.styleable.CalendarViewPager_cc_monthButtonTint)
                ?: typedArray.getColor(R.styleable.CalendarViewPager_cc_monthButtonTint, buttonTintList.defaultColor)
                    .let { ColorStateList.valueOf(it) },
            weekHeaderDividerTint = typedArray.getColorStateList(R.styleable.CalendarViewPager_cc_weekHeaderDividerTint)
                ?: typedArray.getColor(R.styleable.CalendarViewPager_cc_weekHeaderDividerTint, weekHeaderDividerTint.defaultColor)
                    .let { ColorStateList.valueOf(it) },
            weekDayTextTint = typedArray.getColorStateList(R.styleable.CalendarViewPager_cc_weekDayTextTint)
                ?: typedArray.getColor(R.styleable.CalendarViewPager_cc_weekDayTextTint, weekDayTextTint.defaultColor)
                    .let { ColorStateList.valueOf(it) },
            monthDayTextTint = typedArray.getColorStateList(R.styleable.CalendarViewPager_cc_monthDayTextTint)
                ?: typedArray.getColor(R.styleable.CalendarViewPager_cc_monthDayTextTint, monthDayTextTint.defaultColor)
                    .let { ColorStateList.valueOf(it) },
            monthDayOtherTextTint = typedArray.getColorStateList(R.styleable.CalendarViewPager_cc_monthDayOtherTextTint)
                ?: typedArray.getColor(R.styleable.CalendarViewPager_cc_monthDayOtherTextTint, monthDayOtherTextTint.defaultColor)
                    .let { ColorStateList.valueOf(it) },
            weekHeaderDividerHeight = typedArray.getDimensionPixelSize(R.styleable.CalendarViewPager_cc_weekHeaderHeight, weekHeaderDividerHeight),
            monthButtonSize = typedArray.getDimensionPixelSize(R.styleable.CalendarViewPager_cc_monthButtonSize, monthButtonSize),
            monthButtonPadding = typedArray.getDimensionPixelSize(R.styleable.CalendarViewPager_cc_monthButtonPadding, monthButtonPadding),
            monthButtonMargin = typedArray.getDimensionPixelSize(R.styleable.CalendarViewPager_cc_monthButtonMargin, monthButtonMargin),
            monthButtonPrevIconResId = typedArray.getResourceId(R.styleable.CalendarViewPager_cc_monthButtonPrevIcon, R.drawable.ic_month_button_prev_24),
            monthButtonNextIconResId = typedArray.getResourceId(R.styleable.CalendarViewPager_cc_monthButtonNextIcon, R.drawable.ic_month_button_next_24)
        )
    }

    private fun setCurrentItemPrev() {
        val prevItem = currentItem - 1
        if (prevItem >= 0) setCurrentItem(prevItem, true)
    }

    private fun setCurrentItemNext() {
        val count = adapter?.count ?: 0
        val nextItem = currentItem + 1
        if (nextItem < count) setCurrentItem(nextItem, true)
    }

    fun setCurrentItem(month: YearMonth) {
        val position = calendarPagerAdapter?.getPositionForMonth(month) ?: -1
        if (position != -1) {
            val smoothScroll = abs(position - currentItem) < 3
            setCurrentItem(position, smoothScroll)
        }
    }

    fun setCurrentItem(month: YearMonth, smoothScroll: Boolean) {
        val position = calendarPagerAdapter?.getPositionForMonth(month) ?: -1
        if (position != -1) setCurrentItem(position, smoothScroll)
    }

    fun setConfig(config: MonthViewConfig) {
        this.config = config
        calendarPagerAdapter?.setConfig(config)
    }

    override fun setAdapter(adapter: PagerAdapter?) {
        super.setAdapter(adapter)
        calendarPagerAdapter?.pageWrapperCreator = pageWrapperCreator
    }

    var pageWrapperCreator: ((parent: ViewGroup, month: YearMonth) -> WrapperViewHolder)? = null
        set(value) {
            field = value
            calendarPagerAdapter?.pageWrapperCreator = value
        }

    fun setRange(minMonth: YearMonth, maxMonth: YearMonth) {
        calendarPagerAdapter?.also {
            val month = it.getMonth(currentItem)
            it.setRange(minMonth, maxMonth)
            setCurrentItem(month, false)
        }
    }

    fun setOnChangeListener(listener: ((monthView: MonthView) -> Unit)?) {
        calendarPagerAdapter?.onChangeListener = listener
    }

    fun setOnDayClickListener(listener: ((day: YearMonthDay, holder: DayViewHolder) -> Unit)?) {
        calendarPagerAdapter?.onDayClickListener = listener
    }

    private val calendarPagerAdapter: CalendarPagerAdapter?
        get() = this.adapter as? CalendarPagerAdapter
}
