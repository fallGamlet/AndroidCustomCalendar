package com.fallgamlet.customcalendar

import android.R
import android.content.res.ColorStateList
import androidx.core.graphics.ColorUtils

fun colorStateListWithAlphaForPressAndDisabled(color: Int): ColorStateList {
    return ColorStateList(
        arrayOf(
            intArrayOf(R.attr.state_pressed),
            intArrayOf(-R.attr.state_enabled),
            intArrayOf()
        ),
        intArrayOf(
            ColorUtils.setAlphaComponent(color, 128),
            ColorUtils.setAlphaComponent(color, 40),
            color
        )
    )
}
