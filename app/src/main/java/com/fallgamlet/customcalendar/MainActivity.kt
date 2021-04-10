package com.fallgamlet.customcalendar

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction

class MainActivity : AppCompatActivity(),
    NavigationFragment.Listener
{

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fragment = supportFragmentManager.findFragmentById(R.id.content)
        if (fragment == null) showFragment(NavigationFragment(), false)
    }

    override fun onNavigationActionActivated(action: String) {
        showFragment(getFragmentForAction(action))
    }

    private fun getFragmentForAction(action: String): Fragment {
        return when (action) {
            NavigationIds.MONTH_VIEW_EXAMPLE_1 -> MonthViewExample1Fragment()
            NavigationIds.MONTH_VIEW_EXAMPLE_2 -> MonthViewExample2Fragment()
            NavigationIds.CALENDAR_VIEW_PAGER_1 -> CalendarVeiwPagerExample1Fragment()
            NavigationIds.CALENDAR_VIEW_PAGER_2 -> CalendarVeiwPagerExample2Fragment()
            else -> throw IllegalArgumentException("wrong action ID")
        }
    }

    private fun showFragment(fragment: Fragment, addToBackStack: Boolean = true) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.content, fragment)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .also {
                if (addToBackStack) it.addToBackStack(fragment.javaClass.simpleName)
            }
            .commit()
    }
}
