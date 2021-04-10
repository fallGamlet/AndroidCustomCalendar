package com.fallgamlet.calendar

import android.content.res.ColorStateList
import android.graphics.Color
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

data class MonthViewConfig(
    val monthTextAppearance: Int = 0,
    val monthTextTint: ColorStateList = ColorStateList.valueOf(Color.BLACK),
    val buttonTintList: ColorStateList = ColorStateList.valueOf(Color.BLACK),
    val monthFormatter: DateFormat = SimpleDateFormat("LLLL", Locale.getDefault()),
    val monthTextIsVisible: Boolean = true,
    val monthButtonIsVisible: Boolean = true,
    val monthButtonSize: Int = 0,
    val monthButtonPadding: Int = 0,
    val monthButtonMargin: Int = 0,
    val monthButtonNextIconResId: Int = 0,
    val monthButtonPrevIconResId: Int = 0,
    val weekDayTextAppearance: Int = 0,
    val weekDayTextTint: ColorStateList = ColorStateList.valueOf(Color.GRAY),
    val weekDayFormatter: DateFormat = SimpleDateFormat("EEE", Locale.getDefault()),
    val weekFirstDay: Int = Calendar.MONDAY,
    val weekHeaderHeight: Int = 0,
    val weekHeaderDividerTint: ColorStateList = ColorStateList.valueOf(Color.LTGRAY),
    val weekHeaderDividerHeight: Int = 0,
    val dayViewMinSize: Int = 0,
    val monthDayTextAppearance: Int = 0,
    val monthDayTextTint: ColorStateList = ColorStateList.valueOf(Color.BLACK),
    val monthDayOtherTextAppearance: Int = 0,
    val monthDayOtherTextTint: ColorStateList = ColorStateList.valueOf(Color.LTGRAY)
)
