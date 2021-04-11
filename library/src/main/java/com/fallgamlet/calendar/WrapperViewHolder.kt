package com.fallgamlet.calendar

import android.view.View
import android.view.ViewGroup

data class WrapperViewHolder(
    val root: View,
    val contentView: ViewGroup
) {
    constructor(view: ViewGroup): this(view, view)
}
