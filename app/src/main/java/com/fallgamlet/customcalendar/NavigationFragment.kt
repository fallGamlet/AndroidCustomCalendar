package com.fallgamlet.customcalendar

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment

class NavigationFragment: Fragment(R.layout.navigation_actions) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<View>(R.id.exampleMonthViewButton1)
            .setOnClickListener { activateNavigation(NavigationIds.MONTH_VIEW_EXAMPLE_1) }
        view.findViewById<View>(R.id.exampleMonthViewButton2)
            .setOnClickListener { activateNavigation(NavigationIds.MONTH_VIEW_EXAMPLE_2) }
        view.findViewById<View>(R.id.exampleCalendarViewPagerButton1)
            .setOnClickListener { activateNavigation(NavigationIds.CALENDAR_VIEW_PAGER_1) }
        view.findViewById<View>(R.id.exampleCalendarViewPagerButton2)
            .setOnClickListener { activateNavigation(NavigationIds.CALENDAR_VIEW_PAGER_2) }
    }

    private fun activateNavigation(action: String) {
        getListener()?.onNavigationActionActivated(action)
    }

    private fun getListener(): Listener? {
        return targetFragment as? Listener
            ?: parentFragment as? Listener
            ?: activity as? Listener
            ?: context as? Listener
    }

    interface Listener {
        fun onNavigationActionActivated(action: String)
    }

}
