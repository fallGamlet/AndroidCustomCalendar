package com.fallgamlet.calendar

import android.content.res.Resources
import java.util.*
import kotlin.math.ceil

object MonthViewConfigHelper {
    fun defaultConfig(calendar: Calendar, resources: Resources): MonthViewConfig {
        val density = resources.displayMetrics.density
        val monthButtonPadding = density.times(16).toInt()
        return MonthViewConfig(
            weekFirstDay = calendar.firstDayOfWeek,
            weekHeaderDividerHeight = ceil(density).toInt(),
            dayViewMinSize = density.times(24).toInt(),
            weekHeaderHeight = density.times(32).toInt(),
            monthButtonPadding = monthButtonPadding,
            monthButtonSize = density.times(24).toInt() + 2 * monthButtonPadding
        )
    }
}
