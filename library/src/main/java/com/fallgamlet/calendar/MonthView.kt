package com.fallgamlet.calendar

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.isVisible
import androidx.core.view.setMargins
import androidx.core.view.setPadding
import androidx.core.widget.ImageViewCompat
import androidx.core.widget.TextViewCompat
import java.util.*

class MonthView: FrameLayout {

    private val weekDaysCount: Int = 7
    private lateinit var gridLayout: GridLayout
    lateinit var monthTitleView: TextView
        private set
    lateinit var prevMonthButton: ImageView
        private set
    lateinit var nextMonthButton: ImageView
        private set
    lateinit var weekDividerView: View
        private set
    var weekDayViews: Map<Int, DayViewHolder> = emptyMap()
        private set
    var monthDayViews: Map<YearMonthDay, DayViewHolder> = emptyMap()
        private set
    var currentMonth: YearMonth = Calendar.getInstance().toYearMonth()
        private set
    var config = MonthViewConfig()
        private set
    var onChangeListener: ((monthView: MonthView) -> Unit)? = null
    var onPrevClickListener: (() -> Unit)? = null
        set(value) {
            field = value
            updateOnClickListener(prevMonthButton, value)
        }

    var onNextClickListener: (() -> Unit)? = null
        set(value) {
            field = value
            updateOnClickListener(nextMonthButton, value)
        }

    var onDayClickListener: ((day: YearMonthDay, holder: DayViewHolder) -> Unit)? = null
        set(value) {
            field = value
            updateOnDayClickListeners()
        }

    constructor(context: Context): this(context, null)

    constructor(context: Context, attrs: AttributeSet?): super(context, attrs)  {
        initAttrs(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int): super(context, attrs, defStyleAttr)  {
        initAttrs(attrs)
    }

    private fun initAttrs(attrs: AttributeSet?) {
        val calendar = Calendar.getInstance()
        currentMonth = calendar.toYearMonth()
        var config = MonthViewConfigHelper.defaultConfig(calendar, resources)
        attrs?.apply {
            val typedArray = context.obtainStyledAttributes(this, R.styleable.MonthView)
            config = config.applyAttributes(typedArray)
            typedArray.recycle()
        }
        this.config = config
        recreateViews()
        refresh()
    }

    private fun MonthViewConfig.applyAttributes(typedArray: TypedArray): MonthViewConfig {
        return copy(
            weekFirstDay = typedArray.getInt(R.styleable.MonthView_android_firstDayOfWeek, weekFirstDay),
            monthTextIsVisible = typedArray.getBoolean(R.styleable.MonthView_cc_monthTextIsVisible, monthTextIsVisible),
            monthButtonIsVisible = typedArray.getBoolean(R.styleable.MonthView_cc_monthButtonIsVisible, monthButtonIsVisible),
            monthTextAppearance = typedArray.getResourceId(R.styleable.MonthView_cc_monthTextAppearance, android.R.style.TextAppearance_Material_Title),
            weekDayTextAppearance = typedArray.getResourceId(R.styleable.MonthView_cc_weekDayTextAppearance, android.R.style.TextAppearance_Material_Body1),
            monthDayTextAppearance = typedArray.getResourceId(R.styleable.MonthView_cc_monthDayTextAppearance, android.R.style.TextAppearance_Material_Body1),
            monthDayOtherTextAppearance = typedArray.getResourceId(R.styleable.MonthView_cc_monthDayOtherTextAppearance, android.R.style.TextAppearance_Material_Body1),
            monthTextTint = typedArray.getColorStateList(R.styleable.MonthView_cc_monthTextTint)
                ?: typedArray.getColor(R.styleable.MonthView_cc_monthTextTint, monthTextTint.defaultColor)
                    .let { ColorStateList.valueOf(it) },
            buttonTintList = typedArray.getColorStateList(R.styleable.MonthView_cc_monthButtonTint)
                ?: typedArray.getColor(R.styleable.MonthView_cc_monthButtonTint, buttonTintList.defaultColor)
                    .let { ColorStateList.valueOf(it) },
            weekHeaderDividerTint = typedArray.getColorStateList(R.styleable.MonthView_cc_weekHeaderDividerTint)
                ?: typedArray.getColor(R.styleable.MonthView_cc_weekHeaderDividerTint, weekHeaderDividerTint.defaultColor)
                    .let { ColorStateList.valueOf(it) },
            weekDayTextTint = typedArray.getColorStateList(R.styleable.MonthView_cc_weekDayTextTint)
                ?: typedArray.getColor(R.styleable.MonthView_cc_weekDayTextTint, weekDayTextTint.defaultColor)
                    .let { ColorStateList.valueOf(it) },
            monthDayTextTint = typedArray.getColorStateList(R.styleable.MonthView_cc_monthDayTextTint)
                ?: typedArray.getColor(R.styleable.MonthView_cc_monthDayTextTint, monthDayTextTint.defaultColor)
                    .let { ColorStateList.valueOf(it) },
            monthDayOtherTextTint = typedArray.getColorStateList(R.styleable.MonthView_cc_monthDayOtherTextTint)
                ?: typedArray.getColor(R.styleable.MonthView_cc_monthDayOtherTextTint, monthDayOtherTextTint.defaultColor)
                    .let { ColorStateList.valueOf(it) },
            weekHeaderDividerHeight = typedArray.getDimensionPixelSize(R.styleable.MonthView_cc_weekHeaderHeight, weekHeaderDividerHeight),
            monthButtonSize = typedArray.getDimensionPixelSize(R.styleable.MonthView_cc_monthButtonSize, monthButtonSize),
            monthButtonPadding = typedArray.getDimensionPixelSize(R.styleable.MonthView_cc_monthButtonPadding, monthButtonPadding),
            monthButtonMargin = typedArray.getDimensionPixelSize(R.styleable.MonthView_cc_monthButtonMargin, monthButtonMargin),
            monthButtonPrevIconResId = typedArray.getResourceId(R.styleable.MonthView_cc_monthButtonPrevIcon, R.drawable.ic_month_button_prev_24),
            monthButtonNextIconResId = typedArray.getResourceId(R.styleable.MonthView_cc_monthButtonNextIcon, R.drawable.ic_month_button_next_24)
        )
    }

    fun setMonth(month: YearMonth) {
        val isMonthChanged = month != currentMonth
        if (isMonthChanged || weekDayViews.isEmpty()) {
            currentMonth = month
            recreateViews()
            refresh()
        }
        refresh()
    }

    fun setConfig(config: MonthViewConfig) {
        val isRecreateNeeded = this.config.weekFirstDay != config.weekFirstDay
        this.config = config
        if (isRecreateNeeded) recreateViews()
        refresh()
    }

    private fun recreateViews() {
        removeAllViewsInLayout()
        createGridView()
        createMonthHeader()
        createWeekDays()
        createWeekDivider()
        createMonthDays()
    }

    private fun createGridView() {
        gridLayout = GridLayout(context)
        gridLayout.columnCount = weekDaysCount

        addView(gridLayout)
        gridLayout.layoutParams.apply {
            width = LayoutParams.MATCH_PARENT
            height = LayoutParams.MATCH_PARENT
        }
    }

    private fun createMonthHeader() {
        monthTitleView = TextView(context)
        prevMonthButton = ImageView(context)
        nextMonthButton = ImageView(context)
        val layout = LinearLayout(context)
        layout.orientation = LinearLayout.HORIZONTAL
        layout.addView(prevMonthButton)
        layout.addView(monthTitleView)
        layout.addView(nextMonthButton)
        gridLayout.addView(layout)

        val padding = resources.displayMetrics.density.times(8).toInt()
        monthTitleView.setPadding(padding)
        (monthTitleView.layoutParams as LinearLayout.LayoutParams).also {
            it.weight = 1f
            it.width = 0
            it.height = ViewGroup.LayoutParams.WRAP_CONTENT
            it.gravity = Gravity.CENTER
        }
        layout.setVerticalGravity(Gravity.CENTER)
        layout.setHorizontalGravity(Gravity.CENTER)
        (layout.layoutParams as GridLayout.LayoutParams).also {
            it.columnSpec = GridLayout.spec(0, weekDaysCount)
            it.setGravity(Gravity.CENTER)
            it.width = ViewGroup.LayoutParams.MATCH_PARENT
            it.height = ViewGroup.LayoutParams.WRAP_CONTENT
        }
        updateOnClickListener(prevMonthButton, onPrevClickListener)
        updateOnClickListener(nextMonthButton, onNextClickListener)
    }

    private fun createWeekDivider() {
        weekDividerView = View(context)
        gridLayout.addView(weekDividerView)
        (weekDividerView.layoutParams as GridLayout.LayoutParams).also {
            it.columnSpec = GridLayout.spec(0, weekDaysCount)
            it.setGravity(Gravity.TOP)
            it.width = ViewGroup.LayoutParams.MATCH_PARENT
        }
    }

    private fun createWeekDays() {
        val firstDayOfWeek = config.weekFirstDay
        weekDayViews = (0 until weekDaysCount)
            .map {
                val dayOfWeek = (firstDayOfWeek + it) % weekDaysCount
                Pair(dayOfWeek, createDayViewHolder())
            }
            .toMap()

        weekDayViews.values.forEach {
            gridLayout.addView(it.rootView)
            (it.rootView.layoutParams as GridLayout.LayoutParams)
                .apply { this.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f) }
        }
    }

    private fun createMonthDays() {
        monthDayViews = getWeekExtendedMonthDays()
            .map { Pair(it, createDayViewHolder()) }
            .toMap()

        monthDayViews.values.forEach {
            gridLayout.addView(it.rootView)
            (it.rootView.layoutParams as? GridLayout.LayoutParams)?.apply {
                this.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                this.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
            }
        }
    }

    private fun getWeekExtendedMonthDays(): List<YearMonthDay> {
        val calendar = currentMonth.toCalendar(config.weekFirstDay)
        val weeksInYear = calendar.getActualMaximum(Calendar.WEEK_OF_YEAR)
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        var lastWeek = calendar.get(Calendar.WEEK_OF_YEAR)
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH))
        val firstWeek = calendar.get(Calendar.WEEK_OF_YEAR)
        if (firstWeek > lastWeek) lastWeek += weeksInYear
        val weekCount = lastWeek - firstWeek + 1
        val daysCount =  7 * weekCount
        calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
        return (0 until daysCount).map {
            val day = calendar.toYearMonthDay()
            calendar.add(Calendar.DAY_OF_YEAR, 1)
            day
        }
    }

    private fun createDayViewHolder(): DayViewHolder {
        val rootView = FrameLayout(context)
            .apply { layoutParams = LayoutParams(0, 0) }
        val titleView = TextView(context).apply {
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER)
            gravity = Gravity.CENTER
            minWidth = config.dayViewMinSize
            minHeight = config.dayViewMinSize
        }
        rootView.addView(titleView)
        return DayViewHolder(rootView, titleView)
    }

    fun refresh() {
        updateMonthHeaderStyles()
        updateWeekDaysStyles()
        updateMonthDaysStyles()
        onChangeListener?.invoke(this)
    }

    private fun updateMonthHeaderStyles() {
        setTextAppearance(monthTitleView, config.monthTextAppearance)
        monthTitleView.text = config.monthFormatter.format(currentMonth.toCalendar().time)
        monthTitleView.setTextColor(config.monthTextTint)
        monthTitleView.isVisible = config.monthTextIsVisible
        monthTitleView.gravity = Gravity.CENTER

        prevMonthButton.setImageResource(config.monthButtonPrevIconResId)
        nextMonthButton.setImageResource(config.monthButtonNextIconResId)
        listOf(prevMonthButton, nextMonthButton).forEach {
            ImageViewCompat.setImageTintList(it, config.buttonTintList)
            it.isVisible = config.monthButtonIsVisible
            ImageViewCompat.setImageTintList(it, config.buttonTintList)
            it.layoutParams.width = config.monthButtonSize
            it.layoutParams.height = config.monthButtonSize
            it.setPadding(config.monthButtonPadding)
            (it.layoutParams as? MarginLayoutParams)?.setMargins(config.monthButtonMargin)
            it.setBackgroundResource(context.getResIdForAttribute(android.R.attr.selectableItemBackgroundBorderless))
        }
    }

    private fun updateWeekDaysStyles() {
        weekDividerView.setBackgroundColor(config.weekHeaderDividerTint.defaultColor)
        weekDividerView.layoutParams.height = config.weekHeaderDividerHeight

        val calendar =  currentMonth.toCalendar(config.weekFirstDay)
        weekDayViews.entries.forEach {
            val dayOfWeek = it.key
            val holder = it.value
            calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek)
            holder.rootView.minimumHeight = config.weekHeaderHeight
            holder.titleView.apply {
                text = config.weekDayFormatter.format(calendar.time)
                setTextAppearance(this, config.weekDayTextAppearance)
                setTextColor(config.weekDayTextTint)
            }
        }
    }

    private fun updateMonthDaysStyles() {
        val currentMonth = this.currentMonth.toYearMonthDay()
        monthDayViews.forEach {
            val day = it.key
            val holder  = it.value
            val isThisMonthDate = currentMonth.isYearMonthEquals(day)
            val textTint = if (isThisMonthDate) config.monthDayTextTint else config.monthDayOtherTextTint
            val textAppearance = if (isThisMonthDate) config.monthDayTextAppearance else config.monthDayOtherTextAppearance
            holder.titleView.apply {
                setTextAppearance(this, textAppearance)
                setTextColor(textTint)
                text = day.day.toString()
            }
        }
        updateOnDayClickListeners()
    }

    private fun setTextAppearance(textView: TextView, textAppearanceResId: Int) {
        if (textAppearanceResId == 0) return
        TextViewCompat.setTextAppearance(textView, textAppearanceResId)
    }

    private fun updateOnClickListener(view: View, listener: (()->Unit)? = null) {
        view.isEnabled = listener != null
        if (listener != null) view.setOnClickListener { listener.invoke() }
        else view.setOnClickListener(null)
    }

    private fun updateOnDayClickListeners() {
        val hasListener = onDayClickListener != null
        monthDayViews.forEach { entry ->
            if (!hasListener) entry.value.rootView.setOnClickListener(null)
            else entry.value.rootView.setOnClickListener { onDayClickListener?.invoke(entry.key, entry.value) }
        }
    }

    private fun Context.getResIdForAttribute(attrId: Int): Int {
        return kotlin.runCatching {
            val outValue = TypedValue()
            theme.resolveAttribute(attrId, outValue, true)
            return outValue.resourceId
        }.getOrNull() ?: 0
    }
}
